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
package jp.co.acroquest.endosnipe.collector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.acroquest.endosnipe.collector.config.DataCollectorConfig;
import jp.co.acroquest.endosnipe.collector.config.RotateConfig;
import jp.co.acroquest.endosnipe.collector.data.JavelinConnectionData;
import jp.co.acroquest.endosnipe.collector.data.JavelinData;
import jp.co.acroquest.endosnipe.collector.data.JavelinLogData;
import jp.co.acroquest.endosnipe.collector.data.JavelinMeasurementData;
import jp.co.acroquest.endosnipe.collector.log.JavelinLogUtil;
import jp.co.acroquest.endosnipe.collector.request.CommunicationClientRepository;
import jp.co.acroquest.endosnipe.common.Constants;
import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.MeasurementDetail;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.common.jmx.JMXManager;
import jp.co.acroquest.endosnipe.common.logger.ENdoSnipeLogger;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogAccessor;
import jp.co.acroquest.endosnipe.common.util.ResourceDataUtil;
import jp.co.acroquest.endosnipe.common.util.StreamUtil;
import jp.co.acroquest.endosnipe.communicator.accessor.ResourceNotifyAccessor;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.data.dao.JavelinLogDao;
import jp.co.acroquest.endosnipe.data.dao.JavelinMeasurementItemDao;
import jp.co.acroquest.endosnipe.data.db.DBManager;
import jp.co.acroquest.endosnipe.data.entity.JavelinLog;
import jp.co.acroquest.endosnipe.data.entity.JavelinMeasurementItem;
import jp.co.acroquest.endosnipe.data.util.AccumulatedValuesDefinition;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.javelin.parser.ParseException;
import jp.co.acroquest.endosnipe.util.InsertResult;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;
import jp.co.acroquest.endosnipe.util.RotateCallback;

/**
 * {@link JavelinData} をデータベースへ格納するためのクラスです。<br />
 *
 * @author y-komori
 * @author ochiai
 */
public class JavelinDataLogger implements Runnable, LogMessageCodes
{

    private static final String                 JVN_LOG_ENCODING              = "UTF-8";

    private static final String                 KEYWORD_SEQUENCE_ID           = "sequenceId";

    private static final String                 KEYWORD_COLLECTOR_TYPE_NAME   = "collectorTypeName";

    private static final String                 KEYWORD_EVENT_NAME            = "eventName";

    private static final ENdoSnipeLogger        LOGGER                        =
                                                                                ENdoSnipeLogger.getLogger(JavelinDataLogger.class,
                                                                                                          ENdoSnipeDataCollectorPluginProvider.INSTANCE);

    /** メール/SMTPトラップ の通知レベル「正常」を表す */
    public static final int                     ALARM_LEVEL_NORMAL            = 0;

    /** メール/SMTPトラップ の通知レベル「WARN」を表す */
    public static final int                     ALARM_LEVEL_WARN              = 1;

    /** メール/SMTPトラップ の通知レベル「ERROR」を表す */
    public static final int                     ALARM_LEVEL_ERROR             = 2;

    /** トラップ の通知レベルの和名を表す */
    public static final String[]                ALARM_LEVEL_NAME              = {"NORMAL", "WARN",
            "ERROR"                                                           };

    /** アラーム送信用キューのサイズ */
    private static final int                    ALARM_QUEUE_SIZE              = 100;

    private final JavelinDataQueue              queue_                        =
                                                                                new JavelinDataQueue();

    /** 設定 */
    private final DataCollectorConfig           config_;

    /** データベース名をキーに持つ、ローテート設定を保持するマップ */
    private final Map<String, RotateConfig>     rotateConfigMap_;

    /** デフォルトのローテーと設定 */
    private RotateConfig                        defaultRotateConfig_;

    private final CommunicationClientRepository clientRepository_;

    private volatile boolean                    isRunnning_;

    /** 前回の計測値 */
    private final Map<String, ResourceData>     prevResourceDataMap_          =
                                                                                new HashMap<String, ResourceData>();

    /** 前回の計測値(積算を差分に直したもの) */
    private final Map<String, ResourceData>     prevConvertedResourceDataMap_ =
                                                                                new HashMap<String, ResourceData>();

    /** データベース名をキーにした、前回データを挿入したテーブルインデックスを保持するマップ */
    private static Map<String, Integer>         prevTableIndexMap__           =
                                                                                new ConcurrentHashMap<String, Integer>();

    /**
     * Javelinから接続されたときのイベント。
     * 接続データを受け取った時にセットされ、接続前の、全てが0のデータを書き込む際に用いられる。
     * 書き込まれた後、このフィールドはnullに戻される。
     */
    private JavelinConnectionData               connectionData_               = null;

    /** JAVELIN_LOG テーブルを truncate するコールバックメソッド */
    private final RotateCallback                javelinRotateCallback_        =
                                                                                new RotateCallback() {
                                                                                    /**
                                                                                     * {@inheritDoc}
                                                                                     */
                                                                                    public String getTableType()
                                                                                    {
                                                                                        return "JAVELIN_LOG";
                                                                                    }

                                                                                    /**
                                                                                     * {@inheritDoc}
                                                                                     */
                                                                                    public void truncate(
                                                                                            final String database,
                                                                                            final int tableIndex,
                                                                                            final int year)
                                                                                        throws SQLException
                                                                                    {
                                                                                        JavelinLogDao.truncate(database,
                                                                                                               tableIndex,
                                                                                                               year);
                                                                                    }
                                                                                };

    /**
     * 初期化を行います。
     *
     * @param config {@link DataCollectorConfig} オブジェクト
     * @param clientRepository {@link CommunicationClientRepository} オブジェクト
     */
    public JavelinDataLogger(final DataCollectorConfig config,
            final CommunicationClientRepository clientRepository)
    {
        this.rotateConfigMap_ = new HashMap<String, RotateConfig>();
        this.config_ = config;
        this.clientRepository_ = clientRepository;
    }

    /**
     * ローテート設定を追加します。
     *
     * @param rotateConfig ローテート設定
     */
    public void addRotateConfig(final RotateConfig rotateConfig)
    {
        this.rotateConfigMap_.put(rotateConfig.getDatabase(), rotateConfig);
    }

    /**
     * デフォルトのローテート設定を設定します。
     *
     * @param rotateConfig ローテート設定
     */
    public void setDefaultRotateConfig(final RotateConfig rotateConfig)
    {
        this.defaultRotateConfig_ = rotateConfig;
    }

    /**
     * アラームデータを入れるキューを返します。
     *
     * @return キュー
     */
    public JavelinDataQueue getQueue()
    {
        return this.queue_;
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        while (true)
        {
            // 終了チェック
            if (this.isRunnning_ == false && this.queue_.size() == 0)
            {
                break;
            }

            // キューからデータを取り出す
            JavelinData data = this.queue_.take();

            if (data != null)
            {
                // データベースへ書き込み
                logJavelinData(data);
            }
        }

        LOGGER.log(JAVELIN_DATA_LOGGER_STOPPED);
    }

    /**
     * スレッドを停止します。<br />
     *
     * キューに書き込むべきデータが残っている場合、
     * すべてデータベースに書き込んでから終了します。<br />
     */
    public synchronized void stop()
    {
        LOGGER.log(JAVELIN_DATA_LOGGER_STOPPING);
        this.isRunnning_ = false;
    }

    /**
     * 初期化します。<br />
     *
     * @param rotateConfigList エージェント毎のローテート設定のリスト
     */
    protected synchronized void init(final List<RotateConfig> rotateConfigList)
    {
        LOGGER.log(JAVELIN_DATA_LOGGER_STARTED);

        this.rotateConfigMap_.clear();
        for (RotateConfig rotateConfig : rotateConfigList)
        {
            addRotateConfig(rotateConfig);
        }

        this.isRunnning_ = true;
    }

    /**
     * Javelin ログを書き込みます。<br />
     *
     * @param data Javelin ログ
     */
    private void logJavelinData(final JavelinData data)
    {
        if (data instanceof JavelinConnectionData)
        {
            JavelinConnectionData connectionData = (JavelinConnectionData)data;
            String database = connectionData.getDatabaseName();

            if (connectionData.isConnectionData())
            {
                // 接続イベントの場合、
                // それを保持しておき、次回のデータ取り出し時に、計測値が全て0のデータを加える。
                this.connectionData_ = connectionData;
            }
            else if (connectionData.isConnectionData() == false)
            {
                // 切断イベントの場合、計測値が全て0のデータを加える。
                // ただし、接続後すぐに切断した場合は前回のデータが無いので、処理は行わない。
                ResourceData resourceData = this.prevConvertedResourceDataMap_.get(database);
                if (resourceData != null)
                {
                    long measurementTime = connectionData.measurementTime;
                    ResourceData allZeroData =
                                               ResourceDataUtil.createAllZeroResourceData(resourceData,
                                                                                          measurementTime,
                                                                                          false);
                    logResourceData(database, allZeroData, true);
                }
            }
        }
        else if (data instanceof JavelinLogData)
        {
            // Javelin ログの場合
            JavelinLogData logData = (JavelinLogData)data;
            String database = data.getDatabaseName();
            logJavelinLogData(database, logData);

            // Javelinログの判定処理
            alarmJavelinLogData(database, logData);
        }
        else if (data instanceof JavelinMeasurementData)
        {
            // 計測値データの場合
            ResourceData resourceData = ((JavelinMeasurementData)data).getResourceData();
            String database = data.getDatabaseName();

            if (resourceData != null && resourceData.getMeasurementMap() != null)
            {

                // 接続後の最初のデータの場合、接続を表す（計測値が全て0の）データを直前に加える。
                if (this.connectionData_ != null
                        && resourceData.getMeasurementMap().isEmpty() == false)
                {
                    long measurementTime = this.connectionData_.measurementTime;
                    ResourceData allZeroData =
                                               ResourceDataUtil.createAllZeroResourceData(resourceData,
                                                                                          measurementTime,
                                                                                          true);
                    logResourceData(database, allZeroData, true);
                    this.connectionData_ = null;
                }

                logResourceData(database, resourceData);
            }
        }
    }

    /**
     * JVNデータに対して、イベントを送信します。<br />
     *
     * @param database データベース名
     * @param logData {@link JavelinLogData}オブジェクト
     */
    void alarmJavelinLogData(final String database, final JavelinLogData logData)
    {
        JavelinLogAccessor accesor = new JavelinLogAccessor() {
            @Override
            public InputStream getInputStream()
                throws IOException
            {
                byte[] jvnLog = logData.getContents().getBytes(JVN_LOG_ENCODING);
                return new ByteArrayInputStream(jvnLog);
            }
        };
        JavelinParser parser = new JavelinParser(accesor);
        try
        {
            parser.init();
        }
        catch (ParseException ex)
        {
            String message = ex.getMessage();
            LOGGER.log(LogMessageCodes.FAIL_PARSE_JVN_DATA, ex, message);
            return;
        }

        while (true)
        {
            try
            {
                JavelinLogElement element = parser.nextElement();
                if (element == null)
                {
                    break;
                }

                // DataCollectorが参照する各種情報をセットする
                element.setIpAddress(logData.getIpAddress());
                element.setPort(logData.getPort());
                element.setDatabaseName(logData.getDatabaseName());
                element.setAlarmThreshold(logData.getAlarmThreshold());
                element.setCpuAlarmThreshold(logData.getCpuAlarmThreshold());
                element.setLogFileName(logData.getLogFileName());

                String clientId = logData.getClientId();
                if (clientId == null || clientId.equals(""))
                {
                    clientId =
                               JavelinClient.createClientId(logData.getIpAddress(),
                                                            logData.getPort());
                }
            }
            catch (IOException ex)
            {
                String message = ex.getMessage();
                LOGGER.log(LogMessageCodes.FAIL_PARSE_JVN_DATA, ex, message);
            }
            catch (ParseException ex)
            {
                String message = ex.getMessage();
                LOGGER.log(LogMessageCodes.FAIL_PARSE_JVN_DATA, ex, message);
            }
        }
    }

    /**
     * 指定されたデータをデータベースに登録します。<br />
     * @param database データベース名
     * @param resourceData 登録するデータ
     */
    private void logResourceData(final String database, final ResourceData resourceData)
    {
        this.logResourceData(database, resourceData, false);
    }

    /**
     * 指定されたデータをデータベースに登録します。<br />
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @param isConnectionData 接続・切断データかどうか
     */
    private void logResourceData(final String database, final ResourceData resourceData,
            final boolean isConnectionData)
    {
        if (resourceData.getMeasurementMap().isEmpty())
        {
            return;
        }
        RotateConfig rotateConfig = this.rotateConfigMap_.get(database);
        if (rotateConfig == null)
        {
            rotateConfig = defaultRotateConfig_;
        }
        int rotatePeriod = rotateConfig.getMeasureRotatePeriod();
        int rotatePeriodUnit = rotateConfig.getMeasureUnitByCalendar();

        try
        {
            // 可変数系列で新たなデータが加わっている場合、グラフの始まりを表すデータを追加する。
            String prevDataKey = resourceData.clientId;
            ResourceData prevData = this.prevConvertedResourceDataMap_.get(prevDataKey);
            if (prevData != null)
            {
                ResourceData additionalData =
                                              ResourceDataUtil.createAdditionalPreviousData(prevData,
                                                                                            resourceData);

                if (additionalData.getMeasurementMap().size() > 0)
                {
                    insertMeasurementData(database, additionalData, rotatePeriod, rotatePeriodUnit);
                }
            }

            ResourceData convertedResourceData = resourceData;
            if (isConnectionData == false)
            {
                // 積算値が入っている場合、差分にする
                convertedResourceData =
                                        accumulatedValueParser(this.prevResourceDataMap_.get(database),
                                                               resourceData);
            }

            // CPU使用率を計算し、データに加える。
            this.calculateAndAddCpuUsageData(database, convertedResourceData);

            // カバレッジを計算し、データに加える。
            this.calculateAndAddCoverageData(database, convertedResourceData);

            // 割合の値に対して、一定値をかけることで精度を保証する。
            this.convertJmxRatioData(database, convertedResourceData);

            insertMeasurementData(database, convertedResourceData, rotatePeriod, rotatePeriodUnit);

            if (isConnectionData == false)
            {
                if (resourceData.getMeasurementMap() != null
                        && resourceData.getMeasurementMap().size() != 0)
                {
                    this.prevResourceDataMap_.put(database, resourceData);
                    this.prevConvertedResourceDataMap_.put(prevDataKey, convertedResourceData);
                }

                notifyResource(convertedResourceData);

            }
        }
        catch (SQLException ex)
        {
            LOGGER.log(DATABASE_ACCESS_ERROR, ex, ex.getMessage());
        }
    }

    private void insertMeasurementData(final String database,
            final ResourceData convertedResourceData, final int rotatePeriod,
            final int rotatePeriodUnit)
        throws SQLException
    {
        long startTime = System.currentTimeMillis();
        InsertResult result =
                              ResourceDataDaoUtil.insert(database, convertedResourceData,
                                                         rotatePeriod, rotatePeriodUnit,
                                                         config_.getBatchSize(),
                                                         config_.getItemIdCacheSize());
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        if (result.getInsertCount() != 0)
        {
            // IEDC0022=データベースに測定値を登録しました。 データベース名:{0}、経過時間:{1}、登録件数:{2}、キャッシュヒット件数:{3}、キャッシュあふれ回数:{4}
            int cacheHitCount = result.getInsertCount() - result.getCacheMissCount();
            LOGGER.log("IEDC0022", database, elapsedTime, result.getInsertCount(), cacheHitCount,
                       result.getCacheOverflowCount());
        }
    }

    /**
     * 直接割合値を計測している値に対して一定値をかけ、保存する。
     * @param database 
     * 
     * @param resourceData 返還対象の値。
     */
    private void convertJmxRatioData(final String database, final ResourceData resourceData)
        throws SQLException
    {
        // 変換対象の値を特定する。
        List<JavelinMeasurementItem> itemList = JavelinMeasurementItemDao.selectAll(database);
        Set<String> jmxTypeSet = new HashSet<String>();
        for (JavelinMeasurementItem item : itemList)
        {
            // jmxの測定種別を判別する。
            if (item.itemName.indexOf("/jmx/") >= 0)
            {
                jmxTypeSet.add(item.itemName);
            }
        }

        // jmxがなければ何もしない。
        if (jmxTypeSet.size() == 0)
        {
            return;
        }

        // 変換対象の値を取り出す。
        // 一定値をかける。
        for (String itemName : jmxTypeSet)
        {
            Map<String, MeasurementDetail> detailMap = getMultiDetailValue(resourceData, itemName);
            if (detailMap == null)
            {
                continue;
            }

            // それぞれのkey値に対して割合を示すものがあるか捜査する。
            Set<Entry<String, MeasurementDetail>> datailEntrySet = detailMap.entrySet();
            for (Entry<String, MeasurementDetail> detailEntry : datailEntrySet)
            {
                String name = detailEntry.getKey();
                boolean isRatio = false;
                for (String ratioKey : JMXManager.JMX_RATIO_ITEMNAME_ARRAY)
                {
                    if (!name.contains(ratioKey))
                    {
                        continue;
                    }

                    isRatio = true;
                    break;
                }

                if (!isRatio)
                {
                    continue;
                }

                MeasurementDetail detail = detailMap.get(name);
                detail.value =
                               String.valueOf(Double.valueOf(detail.value)
                                       * ResourceDataUtil.PERCENT_CONST
                                       * ResourceDataUtil.PERCENTAGE_DATA_MAGNIFICATION);
            }
        }
    }

    private void notifyResource(final ResourceData convertedResourceData)
    {
        Telegram resourceTelegram =
                                    ResourceNotifyAccessor.getResourceTelgram(convertedResourceData);

        String clientId = convertedResourceData.clientId;
        if (clientId == null || clientId.equals(""))
        {
            clientId =
                       JavelinClient.createClientId(convertedResourceData.ipAddress,
                                                    convertedResourceData.portNum);
        }

        this.clientRepository_.sendTelegramToClient(clientId, resourceTelegram);
    }

    /**
     * 指定されたリソースデータの値を用いてカバレッジを計算し、それをそのデータに追加します。<br />
     *
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @throws SQLException
     */
    private void calculateAndAddCoverageData(final String database, final ResourceData resourceData)
        throws SQLException
    {
        Map<String, Integer> measurementTypeMap = makeMeasurementTypeMap(database);

        // カバレッジの計算に必要な値を取得する。
        long calledMethodCount =
                                 getSingleDetailValue(resourceData, measurementTypeMap,
                                                      Constants.ITEMNAME_CALLEDMETHODCOUNT);
        long convertedMethodCount =
                                    getSingleDetailValue(resourceData, measurementTypeMap,
                                                         Constants.ITEMNAME_CONVERTEDMETHOD);

        // 値が取得できない場合、データの追加は行わない。
        if (calledMethodCount < 0 || convertedMethodCount < 0)
        {
            return;
        }

        double coverage = 0.0;
        if (convertedMethodCount > 0)
        {
            coverage =
                       (double)calledMethodCount / convertedMethodCount
                               * ResourceDataUtil.PERCENT_CONST;
        }

        // カバレッジの値が入ったデータを作成する。
        MeasurementData coverageData =
                                       createCpuUsageMeasurementData(measurementTypeMap,
                                                                     Constants.ITEMNAME_COVERAGE,
                                                                     coverage);

        // 作成したデータを、他のデータの入ったMapに追加する。
        resourceData.getMeasurementMap().put(Constants.ITEMNAME_COVERAGE, coverageData);
    }

    /**
     * 指定されたリソースデータの値を用いてCPU使用率を計算し、それをそのデータに追加します。<br />
     *
     * @param database データベース名
     * @param resourceData 登録するデータ
     * @throws SQLException
     */
    private void calculateAndAddCpuUsageData(final String database, final ResourceData resourceData)
        throws SQLException
    {
        Map<String, Integer> measurementTypeMap = makeMeasurementTypeMap(database);

        // CPU使用率の計算に必要な値を取得する。
        long processorCount =
                              getSingleDetailValue(resourceData, measurementTypeMap,
                                                   Constants.ITEMNAME_SYSTEM_CPU_PROCESSOR_COUNT);
        long javaUpTime =
                          getSingleDetailValue(resourceData, measurementTypeMap,
                                               Constants.ITEMNAME_JAVAUPTIME);
        long sysCputimeTotal =
                               getSingleDetailValue(resourceData, measurementTypeMap,
                                                    Constants.ITEMNAME_SYSTEM_CPU_USERMODE_TIME);
        long sysCputimeSys =
                             getSingleDetailValue(resourceData, measurementTypeMap,
                                                  Constants.ITEMNAME_SYSTEM_CPU_SYSTEM_TIME);
        long sysCputimeIoWait =
                                getSingleDetailValue(resourceData, measurementTypeMap,
                                                     Constants.ITEMNAME_SYSTEM_CPU_IOWAIT_TIME);

        long procCputimeTotal =
                                getSingleDetailValue(resourceData, measurementTypeMap,
                                                     Constants.ITEMNAME_PROCESS_CPU_TOTAL_TIME);
        long procCputimeSys =
                              getSingleDetailValue(resourceData, measurementTypeMap,
                                                   Constants.ITEMNAME_PROCESS_CPU_SYSTEM_TIME);
        long procCputimeIoWait =
                                 getSingleDetailValue(resourceData, measurementTypeMap,
                                                      Constants.ITEMNAME_PROCESS_CPU_IOWAIT_TIME);

        Map<String, MeasurementData> measurementMap = resourceData.getMeasurementMap();

        // システムのCPU使用率の計算に必要な値が取得できている場合、そのデータを追加する。
        if (-1 < sysCputimeTotal && -1 < sysCputimeSys && -1 < processorCount && -1 < javaUpTime)
        {
            double sysCpuusageTotal =
                                      ResourceDataUtil.calcCPUUsage(sysCputimeTotal, javaUpTime,
                                                                    processorCount);
            double sysCpuusageSys =
                                    ResourceDataUtil.calcCPUUsage(sysCputimeSys, javaUpTime,
                                                                  processorCount);
            double sysCpuusageIoWait =
                                       ResourceDataUtil.calcCPUUsage(sysCputimeIoWait, javaUpTime,
                                                                     processorCount);

            MeasurementData sysCpuusageTotalData =
                                                   createCpuUsageMeasurementData(measurementTypeMap,
                                                                                 Constants.ITEMNAME_SYSTEM_CPU_TOTAL_USAGE,
                                                                                 sysCpuusageTotal);
            MeasurementData sysCpuusageSysData =
                                                 createCpuUsageMeasurementData(measurementTypeMap,
                                                                               Constants.ITEMNAME_SYSTEM_CPU_SYSTEM_USAGE,
                                                                               sysCpuusageSys);

            MeasurementData sysCpuusageIoWaitData =
                                                    createCpuUsageMeasurementData(measurementTypeMap,
                                                                                  Constants.ITEMNAME_SYSTEM_CPU_IOWAIT_USAGE,
                                                                                  sysCpuusageIoWait);

            measurementMap.put(Constants.ITEMNAME_SYSTEM_CPU_TOTAL_USAGE, sysCpuusageTotalData);
            measurementMap.put(Constants.ITEMNAME_SYSTEM_CPU_SYSTEM_USAGE, sysCpuusageSysData);
            measurementMap.put(Constants.ITEMNAME_SYSTEM_CPU_IOWAIT_USAGE, sysCpuusageIoWaitData);
        }

        // プロセスのCPU使用率の計算に必要な値が取得できている場合、そのデータを追加する。
        if (-1 < procCputimeTotal && -1 < procCputimeSys && -1 < processorCount && -1 < javaUpTime)
        {
            double procCpuusageTotal =
                                       ResourceDataUtil.calcCPUUsage(procCputimeTotal, javaUpTime,
                                                                     processorCount);
            double procCpuusageSys =
                                     ResourceDataUtil.calcCPUUsage(procCputimeSys, javaUpTime,
                                                                   processorCount);

            double procCpuusageIoWait =
                                        ResourceDataUtil.calcCPUUsage(procCputimeIoWait,
                                                                      javaUpTime, processorCount);

            MeasurementData procCpuusageTotalData =
                                                    createCpuUsageMeasurementData(measurementTypeMap,
                                                                                  Constants.ITEMNAME_PROCESS_CPU_TOTAL_USAGE,
                                                                                  procCpuusageTotal);
            MeasurementData procCpuusageSysData =
                                                  createCpuUsageMeasurementData(measurementTypeMap,
                                                                                Constants.ITEMNAME_PROCESS_CPU_SYSTEM_USAGE,
                                                                                procCpuusageSys);
            MeasurementData procCpuusageIoWaitData =
                                                     createCpuUsageMeasurementData(measurementTypeMap,
                                                                                   Constants.ITEMNAME_PROCESS_CPU_IOWAIT_USAGE,
                                                                                   procCpuusageIoWait);

            measurementMap.put(Constants.ITEMNAME_PROCESS_CPU_TOTAL_USAGE, procCpuusageTotalData);
            measurementMap.put(Constants.ITEMNAME_PROCESS_CPU_SYSTEM_USAGE, procCpuusageSysData);
            measurementMap.put(Constants.ITEMNAME_PROCESS_CPU_IOWAIT_USAGE, procCpuusageIoWaitData);
        }
    }

    /**
     * item_nameとmeasurement_typeの対応を表すマップを生成し、返します。<br />
     *
     * @param database データベース名
     * @return item_nameをキー、measurement_typeを値としたマップ
     * @throws SQLException DBアクセス中に発生した例外
     */
    private Map<String, Integer> makeMeasurementTypeMap(final String database)
        throws SQLException
    {
        // measure_info テーブルの置き換え
        List<JavelinMeasurementItem> measurementItemList;
        measurementItemList = JavelinMeasurementItemDao.selectAll(database);
        Map<String, Integer> measurementTypeMap = new HashMap<String, Integer>();
        for (JavelinMeasurementItem info : measurementItemList)
        {
            measurementTypeMap.put(info.itemName, info.measurementItemId);
        }
        return measurementTypeMap;
    }

    /**
     * 指定されたリソースデータの、指定されたmeasurementTypeを持つデータの値を返します。<br />
     * ただし、データが入っていない場合や、複数系列のものを指定した場合は、-1を返します。<br />
     *
     * @param resourceData リソースデータ
     * @param measurementTypeMap item_nameとmeasurement_typeの対応を表すマップ
     * @param itemName itemName
     * @return 指定されたデータの値
     */
    private long getSingleDetailValue(final ResourceData resourceData,
            final Map<String, Integer> measurementTypeMap, final String itemName)
    {
        long value = -1;
        Map<String, MeasurementData> measurementMap = resourceData.getMeasurementMap();
        MeasurementData measurementData = measurementMap.get(itemName);
        if (measurementData != null)
        {
            Map<String, MeasurementDetail> measurementDetailMap;
            measurementDetailMap = measurementData.getMeasurementDetailMap();
            MeasurementDetail measurementDetail;
            measurementDetail = measurementDetailMap.get(MeasurementData.SINGLE_DETAIL_KEY);
            if (measurementDetail != null)
            {
                value = Long.valueOf(measurementDetail.value).longValue();
            }
        }
        return value;
    }

    /**
     * 指定されたリソースデータの、指定されたmeasurementTypeを持つデータの値をdouble型で返します。<br />
     * ただし、データが入っていない場合や、複数系列のものを指定した場合は、-1を返します。<br />
     *
     * @param resourceData リソースデータ
     * @param measurementTypeMap item_nameとmeasurement_typeの対応を表すマップ
     * @param itemName itemName
     * @return 指定されたデータの値
     */
    private Map<String, MeasurementDetail> getMultiDetailValue(final ResourceData resourceData,
            final String itemName)
    {
        Map<String, MeasurementData> measurementMap = resourceData.getMeasurementMap();
        MeasurementData measurementData = measurementMap.get(itemName);
        Map<String, MeasurementDetail> measurementDetailMap = null;
        if (measurementData != null)
        {
            measurementDetailMap = measurementData.getMeasurementDetailMap();
        }
        return measurementDetailMap;
    }

    /**
     * CPU使用率のデータに対して、指定されたitemNameとvalueをMeasurementDetailとして持つ、<br />
     * MeasurementDataを生成し、返します。<br />
     *
     * @param measurementTypeMap item_nameとmeasurement_typeの対応を表すマップ
     * @param itemName itemName
     * @param cpuUsage データの値
     * @return MeasurementData
     */
    private MeasurementData createCpuUsageMeasurementData(
            final Map<String, Integer> measurementTypeMap, final String itemName,
            final double cpuUsage)
    {
        MeasurementDetail measurementDetail = new MeasurementDetail();
        // 小数点以下の値も保持するため、一定の倍率を掛ける。
        measurementDetail.value =
                                  String.valueOf(cpuUsage
                                          * ResourceDataUtil.PERCENTAGE_DATA_MAGNIFICATION);
        measurementDetail.displayName = "";

        MeasurementData measurementData = new MeasurementData();
        measurementData.itemName = itemName;
        Integer typeObj = measurementTypeMap.get(itemName);
        if (typeObj != null)
        {
            measurementData.measurementType = typeObj.intValue();
        }
        else
        {
            measurementData.measurementType = -1;
        }
        measurementData.valueType = TelegramConstants.BYTE_ITEMMODE_KIND_STRING;
        measurementData.getMeasurementDetailMap().put(MeasurementData.SINGLE_DETAIL_KEY,
                                                      measurementDetail);

        return measurementData;
    }

    /**
     * JVNログデータを保存します。<br />
     *
     * @param database データベース名
     * @param logData {@link JavelinLogData}オブジェクト
     */
    private void logJavelinLogData(final String database, final JavelinLogData logData)
    {
        RotateConfig rotateConfig = this.rotateConfigMap_.get(database);
        if (rotateConfig == null)
        {
            rotateConfig = defaultRotateConfig_;
        }
        int rotatePeriod = rotateConfig.getMeasureRotatePeriod();
        int rotatePeriodUnit = rotateConfig.getMeasureUnitByCalendar();

        JavelinLog javelinLog = createJavelinLog(logData);
        try
        {
            if (DBManager.isDefaultDb() == false)
            {
                // H2以外のデータベースの場合は、パーティショニング処理を行う
                Integer tableIndex = ResourceDataDaoUtil.getTableIndexToInsert(javelinLog.endTime);
                Integer prevTableIndex = prevTableIndexMap__.get(database);
                if (tableIndex.equals(prevTableIndex) == false)
                {
                    Timestamp[] range = JavelinLogDao.getLogTerm(database);
                    if (range.length == 2
                            && (range[1] == null || range[1].before(javelinLog.endTime)))
                    {
                        // 前回の挿入データと今回の挿入データで挿入先テーブルが異なる場合に、ローテート処理を行う
                        // ただし、すでにDBに入っているデータのうち、最新のデータよりも古いデータが入ってきた場合はローテート処理しない
                        boolean truncateCurrent = (prevTableIndex != null);
                        ResourceDataDaoUtil.rotateTable(database, tableIndex, javelinLog.endTime,
                                                        rotatePeriod, rotatePeriodUnit,
                                                        truncateCurrent,
                                                        this.javelinRotateCallback_);
                        prevTableIndexMap__.put(database, tableIndex);
                    }
                }
            }
            JavelinLogDao.insert(database, javelinLog);
        }
        catch (SQLException ex)
        {
            LOGGER.log(DATABASE_ACCESS_ERROR, ex, ex.getMessage());
        }
        StreamUtil.closeStream(javelinLog.javelinLog);

        // 一時ファイルがある場合は削除しておく
        logData.deleteFile();
    }

    /**
     * Javelin ログを、データベースに書き込む形に変換します。<br />
     *
     * @param javelinLogData Javelin ログ
     * @return データベースに書き込む Javelin ログオブジェクト。
     *         ホスト情報が取得できない場合は <code>hostId</code> が <code>-1</code>
     */
    protected JavelinLog createJavelinLog(final JavelinLogData javelinLogData)
    {
        JavelinLog javelinLog = new JavelinLog();
        javelinLog.logFileName = javelinLogData.getLogFileName();

        javelinLog.javelinLog = createContentInputStream(javelinLogData);

        BufferedReader reader =
                                StreamUtil.getBufferedReader(createContentInputStream(javelinLogData));
        try
        {
            String line = reader.readLine();
            List<String> elemList = JavelinLogUtil.csvTokenizeHeader(line);

            // ファイルの1行目を解析して各種属性を設定する
            JavelinLogUtil.parse(javelinLog, elemList);

            // durationを読み込む。
            while ((line = reader.readLine()) != null)
            {
                int beginIndex = line.indexOf(JavelinLogUtil.DURATION_KEY);
                if (beginIndex != 0 || line.length() < JavelinLogUtil.DURATION_KEY.length())
                {
                    continue;
                }
                String durationStr = line.substring(JavelinLogUtil.DURATION_KEY.length());
                JavelinLogUtil.setDuration(javelinLog, durationStr);
                break;
            }
        }
        catch (IOException ex)
        {
            LOGGER.log(IO_EXCEPTION_OCCURED, ex, ex.getMessage());
        }
        finally
        {
            StreamUtil.closeStream(reader);
        }

        return javelinLog;
    }

    /**
     * Javelin ログ内容を {@link InputStream} で返します。<br />
     *
     * @param javelinLogData Javelin ログ
     * @return ログ内容の {@link InputStream}
     */
    protected InputStream createContentInputStream(final JavelinLogData javelinLogData)
    {
        InputStream is = null;
        String contents = javelinLogData.getContents();
        if (contents != null)
        {
            // Javelin ログを文字列で保持している場合
            is = new ByteArrayInputStream(contents.getBytes());
        }
        else
        {
            // Javelin ログをファイルで保持している場合
            is = StreamUtil.getStream(javelinLogData.getFile());
        }
        return is;
    }

    /**
     * データベース名を生成します。<br />
     *
     * @param data Javelin ログ
     * @return データベース名
     */
    protected String createDatabaseName(final JavelinData data)
    {
        final int BUFFER = 32;
        StringBuilder builder = new StringBuilder(BUFFER);
        builder.append("javelin_");
        builder.append(data.getHost());
        builder.append("_");
        builder.append(data.getIpAddress());
        builder.append("_");
        builder.append(data.getPort());
        return builder.toString();
    }

    /**
     * 積算値で入っている値を差分値に変換したものを返す
     * @param prevResourceData 前回の計測値
     * @param resourceData 今回の計測値
     *
     * @return 積算値は差分値に変え、そうでな値はそのままにコピーしたResourceData
     */
    private static ResourceData accumulatedValueParser(final ResourceData prevResourceData,
            final ResourceData resourceData)
    {
        ResourceData newResourceData = new ResourceData();
        newResourceData.measurementTime = resourceData.measurementTime;
        newResourceData.hostName = resourceData.hostName;
        newResourceData.ipAddress = resourceData.ipAddress;
        newResourceData.portNum = resourceData.portNum;
        newResourceData.clientId = resourceData.clientId;

        // ResourceData を、積算値は差分値に変え、そうでな値はそのままにコピーする
        for (MeasurementData measurementData : resourceData.getMeasurementMap().values())
        {
            MeasurementData newMeasurementData = new MeasurementData();
            newMeasurementData.measurementType = measurementData.measurementType;
            newMeasurementData.itemName = measurementData.itemName;
            newMeasurementData.measurementTime = measurementData.measurementTime;
            newMeasurementData.valueType = measurementData.valueType;
            newMeasurementData.displayName = measurementData.displayName;

            // MeasurementData を、積算値は差分値に変え、そうでな値はそのままにコピーする
            for (MeasurementDetail detail : measurementData.getMeasurementDetailMap().values())
            {
                MeasurementDetail newMeasurementDetail = new MeasurementDetail();

                newMeasurementDetail.value = detail.value;
                newMeasurementDetail.displayName = detail.displayName;
                newMeasurementDetail.itemId = detail.itemId;
                newMeasurementDetail.itemName = detail.itemName;
                newMeasurementDetail.itemNum = detail.itemNum;
                newMeasurementDetail.type = detail.type;
                newMeasurementDetail.typeItemName = detail.typeItemName;
                newMeasurementDetail.valueId = detail.valueId;

                // 積算値は差分値に変える
                if (AccumulatedValuesDefinition.isAccumulatedValue(measurementData.itemName,
                                                                   detail.displayType))
                {
                    long prevMeasurementValue =
                                                getPrevValue(prevResourceData, measurementData,
                                                             detail);
                    long resultValue =
                                       Long.valueOf(detail.value).longValue()
                                               - prevMeasurementValue;
                    if (resultValue < 0)
                    {
                        newMeasurementDetail.value = "0";
                    }
                    else
                    {
                        newMeasurementDetail.value = String.valueOf(resultValue);
                    }
                }
                newMeasurementData.addMeasurementDetail(newMeasurementDetail);
            }
            newResourceData.addMeasurementData(newMeasurementData);
        }
        return newResourceData;
    }

    /**
     * measurementData.measurementType に対応する1つ前の計測値を得る
     * @param prevResourceData 1つ前の計測値
     * @param measurementData 計測値
     * @param defaultValue 1つ前の計測値が存在しなかった場合の値
     * @return
     */
    private static long getPrevValue(final ResourceData prevResourceData,
            final MeasurementData measurementData, final MeasurementDetail defaultValue)
    {
        if (prevResourceData == null)
        {
            return Long.valueOf(defaultValue.value).longValue();
        }
        Map<String, MeasurementData> measurementMap = prevResourceData.getMeasurementMap();
        if (measurementMap == null)
        {
            return Long.valueOf(defaultValue.value).longValue();
        }
        MeasurementData prevMeasurementData = measurementMap.get(measurementData.itemName);
        if (prevMeasurementData == null)
        {
            return Long.valueOf(defaultValue.value).longValue();
        }
        Map<String, MeasurementDetail> measurementDetailMap =
                                                              prevMeasurementData.getMeasurementDetailMap();
        if (measurementDetailMap == null)
        {
            return Long.valueOf(defaultValue.value).longValue();
        }
        MeasurementDetail measurementDetail = measurementDetailMap.get(defaultValue.displayName);
        if (measurementDetail == null)
        {
            return Long.valueOf(defaultValue.value).longValue();
        }
        long prevMeasurementValue = Long.valueOf(measurementDetail.value).longValue();
        return prevMeasurementValue;
    }

    /**
     * {@inheritDoc}
     */
    public void collectStarted(final long sequenceId, final String mailTemplateName)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void collectCompleted(final long sequenceId, final String mailTemplateName,
            final String collectorTypeName)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void collectAllCompleted(final long sequenceId, final String mailTemplateName)
    {
    }

}
