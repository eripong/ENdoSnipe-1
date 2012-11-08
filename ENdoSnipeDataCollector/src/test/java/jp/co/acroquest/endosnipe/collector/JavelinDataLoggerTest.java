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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.co.acroquest.endosnipe.collector.config.ConfigurationReader;
import jp.co.acroquest.endosnipe.collector.config.DataCollectorConfig;
import jp.co.acroquest.endosnipe.collector.config.RotateConfig;
import jp.co.acroquest.endosnipe.collector.data.JavelinConnectionData;
import jp.co.acroquest.endosnipe.collector.data.JavelinMeasurementData;
import jp.co.acroquest.endosnipe.collector.exception.InitializeException;
import jp.co.acroquest.endosnipe.collector.request.CommunicationClientRepository;
import jp.co.acroquest.endosnipe.common.entity.MeasurementData;
import jp.co.acroquest.endosnipe.common.entity.ResourceData;
import jp.co.acroquest.endosnipe.common.util.ResourceUtil;
import jp.co.acroquest.endosnipe.communicator.TelegramSender;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.impl.CommunicationClientImpl;
import jp.co.acroquest.endosnipe.data.dao.MeasurementInfoDao;
import jp.co.acroquest.endosnipe.data.entity.MeasurementInfo;
import jp.co.acroquest.endosnipe.util.ResourceDataDaoUtil;
import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.dgic.testing.framework.DJUnitTestCase;

/**
 * {@link JavelinDataLogger} のためのテストクラスです。<br />
 * 
 * @author iida
 */
public class JavelinDataLoggerTest extends DJUnitTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        MockObjectManager.initialize();
    }

    /**
     * リソースデータのDBへの書き込み時に、BottleneckEyeへの通知も行われる事を確認する。
     * @throws Exception 例外が発生した場合
     */
    public void testLogResourceData_Connect()
        throws Exception
    {
        // 準備
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCpuUsageData");
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCoverageData");
        addReturnValue(ResourceDataDaoUtil.class, "insert");
        addReturnValue(JavelinDataLogger.class, "alarmThresholdExceedance");

        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // 実行
        Class<?> cls = javelinDataLogger.getClass();
        Method method = cls.getDeclaredMethod("logResourceData", String.class, ResourceData.class);
        method.setAccessible(true);
        ResourceData resourceData = new ResourceData();
        resourceData.getMeasurementMap().put("/test0", new MeasurementData());
        method.invoke(javelinDataLogger, database, resourceData);

        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        // 検証
        assertEquals(beforeCount + 1, afterCount);

    }

    /**
     * リソースデータのDBへの書き込み時に、BottleneckEyeへの通知も行われる事を確認する。
     * @throws Exception 例外が発生した場合
     */
    public void testLogResourceData_disconnect()
        throws Exception
    {
        // 準備
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCpuUsageData");
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCoverageData");
        addReturnValue(ResourceDataDaoUtil.class, "insert");
        addReturnValue(JavelinDataLogger.class, "alarmThresholdExceedance");

        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // 実行
        Class<?> cls = javelinDataLogger.getClass();
        Method method =
                        cls.getDeclaredMethod("logResourceData", String.class, ResourceData.class,
                                              boolean.class);
        method.setAccessible(true);
        ResourceData resourceData = new ResourceData();
        resourceData.getMeasurementMap().put("/test0", new MeasurementData());
        method.invoke(javelinDataLogger, database, resourceData, true);

        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        // 検証
        assertEquals(beforeCount, afterCount);

    }

    /**
     * 接続電文(接続開始イベント)
     * JavelinConnectionDataの接続開始フラグ(connectionData_)をtrueにし、
     * JavelinDataLogger#logJavelinDataを実行する。
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが一度も呼ばれないこと。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_connect()
        throws Exception
    {
        // 準備
        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // 実行
        Method method = getLogJavelinData();

        boolean connectionData = true;
        JavelinConnectionData javelinLogData = new JavelinConnectionData(connectionData);

        method.invoke(javelinDataLogger, javelinLogData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        assertEquals(beforeCount, afterCount);

    }

    /**
     * 接続電文(切断イベント/前回のデータあり)
     * <li>JavelinConnectionDataの接続開始フラグ(connectionData_)をfalseにする。</li>
     * <li>prevConvertedResourceDataMap_の返り値を設定する</li>
     * <li>JavelinDataLogger#logJavelinDataを実行する。</li>
     * 
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが呼ばれていないこと。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_disConnectPrevData()
        throws Exception
    {
        // 準備
        addReturnValue(ResourceDataDaoUtil.class, "insert");
        addReturnValue(JavelinDataLogger.class, "alarmThresholdExceedance");
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCpuUsageData");
        addReturnValue(JavelinDataLogger.class, "calculateAndAddCoverageData");
        addReturnValue(MeasurementInfoDao.class, "selectAll", new ArrayList<MeasurementInfo>());
        addReturnValue(ResourceDataDaoUtil.class, "insert");

        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // prevConvertedResourceDataMap_を取得する。
        Class<?> cls = javelinDataLogger.getClass();
        Field prevConvertedResourceDataMap = cls.getDeclaredField("prevConvertedResourceDataMap_");
        prevConvertedResourceDataMap.setAccessible(true);

        Map<String, ResourceData> resourceDataMap = new HashMap<String, ResourceData>();
        ResourceData resourceData = new ResourceData();
        resourceData.addMeasurementData(new MeasurementData());
        resourceDataMap.put(database, resourceData);
        prevConvertedResourceDataMap.set(javelinDataLogger, resourceDataMap);

        // 実行
        Method method = getLogJavelinData();

        boolean connectionData = false;
        JavelinConnectionData javelinLogData = new JavelinConnectionData(connectionData);
        javelinLogData.setDatabaseName(database);

        method.invoke(javelinDataLogger, javelinLogData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        assertEquals(beforeCount, afterCount);

    }

    /**
     * 接続電文(切断イベント/前回のデータなし)
     * <li>JavelinConnectionDataの接続開始フラグ(connectionData_)をfalseにする。</li>
     * <li>prevConvertedResourceDataMap_の返り値を設定しない</li>
     * <li>JavelinDataLogger#logJavelinDataを実行する。</li>
     * 
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが呼ばれていないこと。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_disConnectPrevNoData()
        throws Exception
    {
        // 準備
        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // 実行
        Method method = getLogJavelinData();

        boolean connectionData = false;
        JavelinConnectionData javelinLogData = new JavelinConnectionData(connectionData);

        method.invoke(javelinDataLogger, javelinLogData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        assertEquals(beforeCount, afterCount);

    }

    /**
     * リソース通知電文(初回接続)
     * <li>JavelinConnectionDataのconnectionData_フラグをtrueにする。</li>
     * <li>JavelinDataLogger#logJavelinDataに対して、
     * JavelinMeasurementDataを引数にして呼び出す。</li>
     * 
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが2回呼ばれていること。
     * (初期値用の電文、受信した電文のうち、受信した電文のみ送信)
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_measurementFirstEvent()
        throws Exception
    {
        // 準備
        addReturnValue(ResourceDataDaoUtil.class, "insert");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "alarmThresholdExceedance");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "calculateAndAddCpuUsageData");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "calculateAndAddCoverageData");
        addReturnValue(MeasurementInfoDao.class, "selectAll", new ArrayList<MeasurementInfo>());
        addReturnValue(ResourceDataDaoUtil.class, "insert");

        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        Map<String, ResourceData> resourceDataMap = new HashMap<String, ResourceData>();
        ResourceData resourceData = new ResourceData();
        resourceData.addMeasurementData(new MeasurementData());
        resourceDataMap.put(database, resourceData);

        Method method = getLogJavelinData();

        boolean connectionData = true;
        JavelinConnectionData javelinConnectionData = new JavelinConnectionData(connectionData);
        method.invoke(javelinDataLogger, javelinConnectionData);

        // 実行
        JavelinMeasurementData javelinLogData = new JavelinMeasurementData(resourceData);
        javelinLogData.setDatabaseName(database);

        method.invoke(javelinDataLogger, javelinLogData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        assertEquals(beforeCount + 1, afterCount);

    }

    /**
     * リソース通知電文(2回目以降)
     * <li>JavelinDataLogger#logJavelinDataに対して、JavelinConnectionDataを引数にして呼び出す。</li>
     * <li>JavelinDataLogger#logJavelinDataに対して、JavelinMeasurementDataを引数にして呼び出す。</li>
     * 
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが1回呼ばれていること。
     * (受信した電文)
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_testLogResourceData_measurementSecondEvent()
        throws Exception
    {
        // 準備
        addReturnValue(ResourceDataDaoUtil.class, "insert");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "alarmThresholdExceedance");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "calculateAndAddCpuUsageData");
        setReturnValueAtAllTimes(JavelinDataLogger.class, "calculateAndAddCoverageData");
        addReturnValue(MeasurementInfoDao.class, "selectAll", new ArrayList<MeasurementInfo>());
        addReturnValue(ResourceDataDaoUtil.class, "insert");

        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);

        // prevConvertedResourceDataMap_を取得する。
        Class<?> cls = javelinDataLogger.getClass();
        Field prevConvertedResourceDataMap = cls.getDeclaredField("prevConvertedResourceDataMap_");
        prevConvertedResourceDataMap.setAccessible(true);

        Map<String, ResourceData> resourceDataMap = new HashMap<String, ResourceData>();
        ResourceData resourceData = new ResourceData();
        resourceData.addMeasurementData(new MeasurementData());
        resourceDataMap.put(database, resourceData);
        prevConvertedResourceDataMap.set(javelinDataLogger, resourceDataMap);

        // 実行
        Method method = getLogJavelinData();

        JavelinMeasurementData javelinLogData = new JavelinMeasurementData(resourceData);
        javelinLogData.setDatabaseName(database);

        method.invoke(javelinDataLogger, javelinLogData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        assertEquals(beforeCount + 1, afterCount);

    }

    /**
     * 対象外の電文(接続電文、Javelinログ通知電文、リソース通知電文以外)
     * <li>JavelinConnectionDataを継承した、MockJavelinConnectionDataを作成する。</li>
     * <li>JavelinDataLogger#logJavelinDataを実行する。</li>
     * 
     * 
     * [結果]
     * CommunicationServerImpl#sendTelegramが呼ばれていないこと。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testLogJavelinData_testLogResourceData_otherData()
        throws Exception
    {
        // 準備
        int beforeCount = getCallCount("CommunicationServerImpl", "sendTelegram");

        String database = "database";
        JavelinDataLogger javelinDataLogger = createJavelinDataLogger(database);
        Method method = getLogJavelinData();

        // 実行
        MockJavelinData javelinData = new MockJavelinData();
        method.invoke(javelinDataLogger, javelinData);

        // 検証
        int afterCount = getCallCount("CommunicationServerImpl", "sendTelegram");
        assertEquals(beforeCount, afterCount);

    }

    /**
     * JavelinDataLoggerオブジェクトを作成します。
     * @param dataBase データベース名
     * @return {@link JavelinDataLogger}オブジェクト
     * @throws IOException 入出力例外が発生した場合
     * @throws InitializeException 初期化時に例外が発生した場合
     */
    private JavelinDataLogger createJavelinDataLogger(final String dataBase)
        throws IOException,
            InitializeException
    {
        File file = ResourceUtil.getResourceAsFile(getClass(), "dataCollector.conf");
        DataCollectorConfig config = ConfigurationReader.load(file.getAbsolutePath());
        JavelinDataLogger javelinDataLogger =
                                              new JavelinDataLogger(config,
                                                                    new ClientRepositoryMock());
        RotateConfig rotateConfig = new RotateConfig();
        rotateConfig.setDatabase(dataBase);
        javelinDataLogger.addRotateConfig(rotateConfig);
        return javelinDataLogger;
    }

    /**
     * JavelinDataLogger#logJavelinDataを取得します。
     * @return JavelinDataLogger#logJavelinDataメソッド
     * @throws NoSuchMethodException メソッドが見つからない場合
     * @throws ClassNotFoundException クラスが見つからない場合
     */
    private Method getLogJavelinData()
        throws NoSuchMethodException,
            ClassNotFoundException
    {
        Class<?> cls = Class.forName("jp.co.acroquest.endosnipe.collector.JavelinDataLogger");
        Method[] methods = cls.getDeclaredMethods();
        Method returnMethod = null;
        for (Method tmpMethod : methods)
        {
            if ("logJavelinData".equals(tmpMethod.getName()))
            {
                returnMethod = tmpMethod;
                returnMethod.setAccessible(true);
                return returnMethod;
            }
        }
        return null;
    }

    private static class ClientRepositoryMock implements CommunicationClientRepository
    {
        /**
         * {@inheritDoc}
         */
        public void sendTelegramToClient(final String clientId, final Telegram telegram)
        {

        }

        /**
         * {@inheritDoc}
         */
        public TelegramSender getTelegramSender(final String clientId)
        {
            return new CommunicationClientImpl("Thread-Name");
        }

    }
}
