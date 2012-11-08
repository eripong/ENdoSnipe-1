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
package jp.co.acroquest.endosnipe.web.dashboard.listener.collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.communicator.TelegramListener;
import jp.co.acroquest.endosnipe.communicator.accessor.ResourceNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.MeasurementConstants;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.web.dashboard.manager.EventManager;
import jp.co.acroquest.endosnipe.web.dashboard.manager.MessageSender;
import jp.co.acroquest.endosnipe.web.dashboard.manager.ResourceSender;

import org.wgp.manager.WgpDataManager;

/**
 * DataCollectorから計測データの通知を受け、クライアントに計測データを返すためのリスナです。
 * @author eriguchi
 *
 */
public class CollectorListener implements TelegramListener
{

    private static final Map<Integer, List<Integer>> SAME_DATA_MAP;

    /** データベース名 */
    private final String databaseName_;

    static
    {
        SAME_DATA_MAP = new HashMap<Integer, List<Integer>>();
        List<Integer> typeList = new ArrayList<Integer>();
        typeList.add(MeasurementConstants.TYPE_TURNAROUNDTIMECOUNT);
        typeList.add(MeasurementConstants.TYPE_PROC_RES_TOTAL_COUNT_EXCLUSION_SQL);
        typeList.add(MeasurementConstants.TYPE_PROC_RES_TOTAL_COUNT_ONLY_SQL);
        SAME_DATA_MAP.put(MeasurementConstants.TYPE_TURNAROUNDTIMECOUNT, typeList);
        typeList = new ArrayList<Integer>();
        typeList.add(MeasurementConstants.TYPE_TURNAROUNDTIME);
        typeList.add(MeasurementConstants.TYPE_PROC_RES_TIME_AVERAGE_EXCLUSION_SQL);
        typeList.add(MeasurementConstants.TYPE_PROC_RES_TIME_AVERAGE_ONLY_SQL);
        SAME_DATA_MAP.put(MeasurementConstants.TYPE_TURNAROUNDTIME, typeList);
        typeList = new ArrayList<Integer>();
        typeList.add(MeasurementConstants.TYPE_PHYSICALMEMORY_FREE);
        typeList.add(MeasurementConstants.TYPE_SYS_PHYSICALMEM_USED);
        SAME_DATA_MAP.put(MeasurementConstants.TYPE_PHYSICALMEMORY_FREE, typeList);

    }

    /** メッセージ送信用オブジェクトです。 */
    private final MessageSender messageSender_;

    /** エージェントID */
    private final int agentId_;

    /**
     * コンストラクタです。
     * @param messageSender {@link MessageSender}オブジェクト
     * @param agentId エージェントID
     */
    public CollectorListener(final MessageSender messageSender, final int agentId,
            final String databaseName)
    {
        this.messageSender_ = messageSender;
        this.agentId_ = agentId;
        this.databaseName_ = databaseName;
    }

    /**
     * 計測通知電文を受け、クライアントに計測データを返します。
     * @param telegram 計測通知電文
     * @return 応答電文(nullを返す。)
     */
    public Telegram receiveTelegram(final Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        if (header.getByteTelegramKind() == TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY
                && header.getByteRequestKind() == TelegramConstants.BYTE_REQUEST_KIND_RESPONSE)
        {
            ResourceData resourceData =
                                        ResourceNotifyAccessor.createResourceData(telegram,
                                                                                  this.databaseName_);

            EventManager eventManager = EventManager.getInstance();
            WgpDataManager dataManager = eventManager.getWgpDataManager();
            ResourceSender resourceSender = eventManager.getResourceSender();
            if (dataManager == null || resourceSender == null)
            {
                return null;
            }

            resourceSender.send(resourceData);

            //            dataManager.getData(dataGroupId, objectId)
            //            
            //            WgpMessageInbound 
            //
            //            Map<Integer, List<MeasurementData>> measurementDataMap =
            //                                                                     createMeasurementDataMap(resourceData);
            //
            //            Map<String, MeasurementSetting> matchSettingMap =
            //                                                              getMatchSettingMap(measurementDataMap);
            //
            //            sendNotifyEntry(measurementDataMap, matchSettingMap);
            //            MeasurementNotifyEntity cpuMeasurementEntity = createMeasurementEntity(
            //                    measurementDataMap, 1, Arrays.asList(new Integer[]
            //                    { MeasurementConstants.TYPE_SYS_CPU_TOTAL_USAGE,
            //                            MeasurementConstants.TYPE_SYS_CPU_SYS_USAGE }));
            //
            //            String result = JSON.encode(cpuMeasurementEntity);
            //            this.messageSender_.send(result);

        }

        return null;
    }

    //    /**
    //     * クライアントに計測情報を通知する。
    //     * @param measurementDataMap
    //     * @param matchSettingMap
    //     */
    //    private void sendNotifyEntry(final Map<Integer, List<MeasurementData>> measurementDataMap,
    //            final Map<String, MeasurementSetting> matchSettingMap)
    //    {
    //        for (Map.Entry<String, MeasurementSetting> matchSettingEntry : matchSettingMap.entrySet())
    //        {
    //            String clientId = matchSettingEntry.getKey();
    //            MeasurementSetting matchSetting = matchSettingEntry.getValue();
    //            List<MeasurementNotifyEntity> notifyEntryList =
    //                                                            new ArrayList<MeasurementNotifyEntity>();
    //            for (Entry<Integer, Map<Integer, Map<Integer, String>>> graphEntry : matchSetting.getGraphMap().entrySet())
    //            {
    //                Map<Integer, Map<Integer, String>> graphMap = graphEntry.getValue();
    //                Integer graphId = graphEntry.getKey();
    //                for (Map.Entry<Integer, Map<Integer, String>> agentEntry : graphMap.entrySet())
    //                {
    //                    Map<Integer, String> measurementTypeMap = agentEntry.getValue();
    //                    MeasurementNotifyEntity notifyEntity =
    //                                                           createMeasurementEntity(measurementDataMap,
    //                                                                                   graphId.intValue(),
    //                                                                                   measurementTypeMap);
    //                    notifyEntryList.add(notifyEntity);
    //                }
    //            }
    //            String result = JSON.encode(notifyEntryList);
    //            this.messageSender_.send(clientId, result);
    //        }
    //    }
    //
    //    private Map<Integer, List<MeasurementData>> createMeasurementDataMap(
    //            final ResourceData resourceData)
    //    {
    //        Map<Integer, List<MeasurementData>> measurementDataMap =
    //                                                                 new HashMap<Integer, List<MeasurementData>>();
    //        Map<Integer, jp.co.acroquest.endosnipe.common.entity.MeasurementData> measurementMap =
    //                                                                                               resourceData.getMeasurementMap();
    //        //max
    //        jp.co.acroquest.endosnipe.common.entity.MeasurementData systemPhisycalmemMaxMeasurementData =
    //                                                                                                      measurementMap.get(MeasurementConstants.TYPE_SYS_PHYSICALMEM_MAX);
    //
    //        MeasurementDetail systemPhisycalmemMaxMeasurementDetail =
    //                                                                  systemPhisycalmemMaxMeasurementData.getMeasurementDetailMap().get("");
    //
    //        for (jp.co.acroquest.endosnipe.common.entity.MeasurementData measurementData : measurementMap.values())
    //        {
    //            List<Integer> measurementTypeList = SAME_DATA_MAP.get(measurementData.measurementType);
    //            if (measurementTypeList == null)
    //            {
    //                measurementTypeList = new ArrayList<Integer>();
    //                measurementTypeList.add(measurementData.measurementType);
    //            }
    //            for (Integer mesurementType : measurementTypeList)
    //            {
    //                for (MeasurementDetail detail : measurementData.getMeasurementDetailMap().values())
    //                {
    //
    //                    //SQLを取り除くかどうかを判断し取捨選択する。
    //                    String tmpItemName = detail.displayName;
    //                    if (tmpItemName == null)
    //                    {
    //                        tmpItemName = "";
    //                    }
    //
    //                    if (TermMeasurementDataProcessor.EXCLUSION_SQL_TYPES.contains(String.valueOf(mesurementType)))
    //                    {
    //                        if (tmpItemName.indexOf(TermMeasurementDataProcessor.SQL_TYPE_STRING) >= 0)
    //                        {
    //                            continue;
    //                        }
    //                    }
    //
    //                    if (TermMeasurementDataProcessor.ONLY_SQL_TYPES.contains(String.valueOf(mesurementType)))
    //                    {
    //                        if (tmpItemName.indexOf(TermMeasurementDataProcessor.SQL_TYPE_STRING) == -1)
    //                        {
    //                            continue;
    //                        }
    //                    }
    //
    //                    MeasurementData clientMeasurementData = new MeasurementData();
    //                    clientMeasurementData.measurement_item = new MeasurementDetailData();
    //                    clientMeasurementData.timestamp = resourceData.measurementTime;
    //                    clientMeasurementData.measurement_item.measurement_id = detail.itemId;
    //                    clientMeasurementData.measurement_item.measurement_type = mesurementType;
    //                    clientMeasurementData.measurement_item.item_name = tmpItemName;
    //
    //                    if (mesurementType == MeasurementConstants.TYPE_SYS_CPU_TOTAL_USAGE
    //                            || mesurementType == MeasurementConstants.TYPE_SYS_CPU_SYS_USAGE
    //                            || mesurementType == MeasurementConstants.TYPE_PROC_CPU_TOTAL_USAGE
    //                            || mesurementType == MeasurementConstants.TYPE_PROC_CPU_SYS_USAGE)
    //                    {
    //                        double convertValue =
    //                                              EventUtil.makeDoubleMeasurementData(detail.value.doubleValue());
    //                        clientMeasurementData.measurement_item.measurement_value =
    //                                                                                   String.valueOf(convertValue);
    //                    }
    //                    else if (mesurementType == MeasurementConstants.TYPE_SYS_PHYSICALMEM_USED)
    //                    {
    //                        //freeをusedに変換する。
    //                        long systemPhysicalMaxValue =
    //                                                      systemPhisycalmemMaxMeasurementDetail.value.longValue();
    //                        long usedPhysicalMemoryData =
    //                                                      EventUtil.makeUsedPhysicalMemoryData(systemPhysicalMaxValue,
    //                                                                                           detail.value.longValue());
    //
    //                        clientMeasurementData.measurement_item.measurement_value =
    //                                                                                   String.valueOf(usedPhysicalMemoryData);
    //                    }
    //                    // jmxにおける処理。
    //                    else if (mesurementType > 255)
    //                    {
    //                        // HitRatioを検出する。
    //                        boolean isRatio = false;
    //                        for (String ratioName : JMXManager.JMX_RATIO_ITEMNAME_ARRAY)
    //                        {
    //                            if (tmpItemName == null)
    //                            {
    //                                break;
    //                            }
    //                            if (!tmpItemName.contains(ratioName))
    //                            {
    //                                continue;
    //                            }
    //                            isRatio = true;
    //                            break;
    //                        }
    //
    //                        if (isRatio)
    //                        {
    //                            double convertValue =
    //                                                  EventUtil.makeDoubleMeasurementData(detail.value.doubleValue());
    //                            clientMeasurementData.measurement_item.measurement_value =
    //                                                                                       String.valueOf(convertValue);
    //                        }
    //                        else
    //                        {
    //                            clientMeasurementData.measurement_item.measurement_value =
    //                                                                                       String.valueOf(detail.value);
    //                        }
    //                    }
    //                    else
    //                    {
    //                        clientMeasurementData.measurement_item.measurement_value =
    //                                                                                   String.valueOf(detail.value);
    //
    //                    }
    //
    //                    Integer measuementTypeInteger =
    //                                                    Integer.valueOf(clientMeasurementData.measurement_item.measurement_type);
    //                    List<MeasurementData> list = measurementDataMap.get(measuementTypeInteger);
    //                    if (list == null)
    //                    {
    //                        list = new ArrayList<MeasurementData>();
    //                        measurementDataMap.put(measuementTypeInteger, list);
    //                    }
    //                    list.add(clientMeasurementData);
    //                }
    //            }
    //        }
    //
    //        return measurementDataMap;
    //    }
    //
    //    private MeasurementNotifyEntity createMeasurementEntity(
    //            final Map<Integer, List<MeasurementData>> measurementDataMap, final int graphId,
    //            final Map<Integer, String> measuremtTypeMap)
    //    {
    //        MeasurementNotifyEntity measurementEntity = new MeasurementNotifyEntity();
    //        measurementEntity.event_id = EventConstants.EVENT_NOTIFY_MEASUREMENT_ITEM;
    //        measurementEntity.graph_id = graphId;
    //        measurementEntity.server_ids = new int[1];
    //        measurementEntity.server_ids[0] = 1;
    //
    //        List<MeasurementData> graphDataList = new ArrayList<MeasurementData>();
    //
    //        for (Entry<Integer, String> measuremtTypeEntry : measuremtTypeMap.entrySet())
    //        {
    //            Integer measurementType = measuremtTypeEntry.getKey();
    //            // JMXから値を取得するときは、すべてのループを行い、該当の項目のみを設定する。
    //            if (measurementType == MeasurementConstants.TYPE_JMX)
    //            {
    //                String itemName = measuremtTypeEntry.getValue();
    //                setJMXMeasurementData(measurementDataMap, graphDataList, itemName);
    //                continue;
    //            }
    //            List<MeasurementData> list = measurementDataMap.get(Integer.valueOf(measurementType));
    //            if (list == null)
    //            {
    //                continue;
    //            }
    //            for (MeasurementData measurementData : list)
    //            {
    //                graphDataList.add(measurementData);
    //            }
    //        }
    //        measurementEntity.measurement_data =
    //                                             graphDataList.toArray(new MeasurementData[graphDataList.size()]);
    //        return measurementEntity;
    //    }
    //
    //    /**
    //     * JMXから取得するデータを設定する。
    //     * @param measurementDataMap 通知に含まれる計測データ
    //     * @param graphDataList グラフ描画用データ
    //     * @param itemName 項目名
    //     */
    //    private void setJMXMeasurementData(
    //            final Map<Integer, List<MeasurementData>> measurementDataMap,
    //            final List<MeasurementData> graphDataList, final String itemName)
    //    {
    //        for (Entry<Integer, List<MeasurementData>> measuermentDataEntry : measurementDataMap.entrySet())
    //        {
    //            Integer measurementDataType = measuermentDataEntry.getKey();
    //            if (measurementDataType > 255)
    //            {
    //                List<MeasurementData> measuermentDataList = measuermentDataEntry.getValue();
    //                for (MeasurementData measurmentData : measuermentDataList)
    //                {
    //                    if (measurmentData.measurement_item.item_name.startsWith(itemName))
    //                    {
    //                        graphDataList.add(measurmentData);
    //                    }
    //                }
    //
    //            }
    //        }
    //    }
    //
    //    /**
    //     * 管理クラスの保持するクライアントの設定情報から、引数で指定されたデータと一致するものを返します。
    //     * @param measurementDataMap 比較対象のデータ
    //     * @return 引数で指定されたデータと一致するクライアント毎の設定情報
    //     */
    //    private Map<String, MeasurementSetting> getMatchSettingMap(
    //            final Map<Integer, List<MeasurementData>> measurementDataMap)
    //    {
    //        Map<String, MeasurementSetting> settingMap = new HashMap<String, MeasurementSetting>();
    //        EventManager manager = EventManager.getInstance();
    //        Map<String, MeasurementSetting> clientSettings = manager.getCliantSettings();
    //
    //        Set<Integer> measurementTypeSet = measurementDataMap.keySet();
    //
    //        for (Entry<String, MeasurementSetting> clientEntry : clientSettings.entrySet())
    //        {
    //            String clientId = clientEntry.getKey();
    //            MeasurementSetting clientSetting = clientEntry.getValue();
    //            MeasurementSetting matchSetting = getMatchSetting(clientSetting, measurementTypeSet);
    //            if (matchSetting != null)
    //            {
    //                settingMap.put(clientId, matchSetting);
    //            }
    //        }
    //        return settingMap;
    //    }
    //
    //    /**
    //     * クライアントの設定情報から計測項目IDの一致する設定情報を取得します。
    //     * @param clientSetting クライアントの設定情報
    //     * @param measurementTypeSet 計測項目IDのセット
    //     * @return クライアントの設定情報から計測項目IDの一致する設定情報
    //     */
    //    private MeasurementSetting getMatchSetting(final MeasurementSetting clientSetting,
    //            final Set<Integer> measurementTypeSet)
    //    {
    //        MeasurementSetting matchSetting = null;
    //
    //        Map<Integer, Map<Integer, Map<Integer, String>>> graphMaps = clientSetting.getGraphMap();
    //        for (Entry<Integer, Map<Integer, Map<Integer, String>>> clientGraphEntry : graphMaps.entrySet())
    //        {
    //            Integer graphId = clientGraphEntry.getKey();
    //            Map<Integer, String> clientMeasurementTypeSet =
    //                                                            getClientMeasurementTypeSet(graphId,
    //                                                                                        clientSetting);
    //            if (clientMeasurementTypeSet == null)
    //            {
    //                continue;
    //            }
    //            for (Entry<Integer, String> clientMeasurementEntry : clientMeasurementTypeSet.entrySet())
    //            {
    //                Integer measurementType = clientMeasurementEntry.getKey();
    //                if (measurementType != MeasurementConstants.TYPE_JMX
    //                        && measurementTypeSet.contains(measurementType) == false)
    //                {
    //                    continue;
    //                }
    //
    //                if (matchSetting == null)
    //                {
    //                    matchSetting = new MeasurementSetting();
    //                }
    //                String itemName = null;
    //                if (measurementType == MeasurementConstants.TYPE_JMX)
    //                {
    //                    itemName = clientMeasurementEntry.getValue();
    //                }
    //                matchSetting.addMeasurementType(graphId, this.agentId_, measurementType, itemName);
    //            }
    //        }
    //        return matchSetting;
    //    }
    //
    //    /**
    //     * グラフIdとクライアントの設定情報から計測項目IDの一致する設定情報を取得する。
    //     *
    //     * @param graphId 測定対象のグラフId
    //     * @param clientSetting クライアントの設定情報
    //     * @return　クライアントの設定情報から計測項目IDの一致する設定情報
    //     */
    //    private Map<Integer, String> getClientMeasurementTypeSet(final Integer graphId,
    //            final MeasurementSetting clientSetting)
    //    {
    //        boolean isAutoNotify = clientSetting.isAutoNotify(graphId);
    //        if (isAutoNotify == false)
    //        {
    //            return null;
    //        }
    //        Map<Integer, Map<Integer, String>> agentMap = clientSetting.getAgentMap(graphId);
    //        Map<Integer, String> storeMeasurementTypeMap = agentMap.get(this.agentId_);
    //        if (storeMeasurementTypeMap == null)
    //        {
    //            return null;
    //        }
    //        Map<Integer, String> clientMeasurementTypeSet =
    //                                                        new TreeMap<Integer, String>(
    //                                                                                     storeMeasurementTypeMap);
    //        return clientMeasurementTypeSet;
    //    }
}
