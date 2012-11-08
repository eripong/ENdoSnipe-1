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
package jp.co.acroquest.endosnipe.javelin.communicate.telegram;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.communicator.util.TelegramAssertionUtil;
import jp.co.acroquest.endosnipe.javelin.common.ConfigUpdater;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.CreateTelegramUtil;
import jp.co.acroquest.test.util.JavelinTestUtil;
import junit.framework.TestCase;

/**
 * サーバプロパティ更新用テストクラス
 * @author fujii
 *
 */
public class UpdatRequestTelegramListenerTest extends TestCase implements TelegramConstants
{
    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    private static String[] resultObj_ = {"javelin.alarmThreshold", "javelin.alarmCpuThreshold",
            "javelin.alarmException", "javelin.log.args", "javelin.log.args.detail",
            "javelin.log.args.detail.depth", "javelin.log.return", "javelin.log.return.detail",
            "javelin.log.return.detail.depth", "javelin.log.stacktrace",
            "javelin.log.http.session", "javelin.log.http.session.detail",
            "javelin.log.http.session.detail.depth", "javelin.log.mbeaninfo",
            "javelin.log.mbeaninfo.root", "javelin.event.level", "javelin.threadModel",
            "javelin.leak.collection.monitor", "javelin.leak.collectionSizeThreshold",
            "javelin.leak.collectionSizeOut", "javelin.leak.class.histo",
            "javelin.leak.class.histo.interval", "javelin.leak.class.histo.max",
            "javelin.leak.class.histo.gc", "javelin.linearsearch.monitor",
            "javelin.linearsearch.size", "javelin.linearsearch.ratio", "javelin.net.input.monitor",
            "javelin.net.output.monitor", "javelin.file.input.monitor",
            "javelin.file.output.monitor", "javelin.finalizationCount.monitor",
            "javelin.interval.monitor", "javelin.thread.monitor",
            "javelin.thread.monitor.interval", "javelin.thread.monitor.depth",
            "javelin.thread.block.threshold", "javelin.thread.blocktime.threshold",
            "javelin.thread.dump.monitor", "javelin.thread.dump.interval",
            "javelin.thread.dump.threadnum", "javelin.thread.dump.cpu", "javelin.fullgc.monitor",
            "javelin.fullgc.threshold", "javelin.thread.deadlock.monitor",
            "javelin.minimumAlarmInterval", "javelin.tat.monitor", "javelin.tat.keepTime",
            "javelin.httpSessionCount.monitor", "javelin.httpSessionSize.monitor",
            "javelin.concurrent.monitor", "javelin.timeout.monitor",
            "javelin.log4j.printstack.level", "javelin.call.tree.enable", "javelin.call.tree.max",
            "javelin.log.enable", "javelin.resource.collectSystemResources",
            "javelin.record.invocation.sendFullEvent", "javelin.jdbc.enable",
            "javelin.jdbc.recordExecPlan", "javelin.jdbc.execPlanThreshold",
            "javelin.jdbc.recordDuplJdbcCall", "javelin.jdbc.recordBindVal",
            "javelin.jdbc.stringLimitLength", "javelin.jdbc.sqlcount.monitor",
            "javelin.jdbc.sqlcount", "javelin.jdbc.oracle.allowSqlTrace",
            "javelin.jdbc.postgres.verbosePlan", "javelin.jdbc.record.stackTrace",
            "javelin.jdbc.record.stacktraceThreashold"};

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        // オプションファイルから、オプション設定を読み込む。
        MockObjectManager.initialize();
        JavelinTestUtil.camouflageJavelinConfig(getClass(), "/telegram/conf/javelin.properties");
        this.config_ = new JavelinConfig();
        SystemLogger.initSystemLog(this.config_);

        // 修正を加えたクラスのインスタンスを作成する。
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        Class<JavelinConfigUtil> cls = JavelinConfigUtil.class;
        Method method = cls.getDeclaredMethod("load", (Class[])null);
        method.setAccessible(true);

        method.invoke(configUtil, (Object[])null);

    }

    /**
     * @throws Exception 
     * @test サーバプロパティ設定電文-取得応答電文：パターン１(項目:4-1-3)
     * @condition 
     * @result
     */
    public void testReceiveTelegram_Update_Pattern1()
        throws Exception
    {
        // 準備
        UpdateRequestTelegramListener telegramListener = new UpdateRequestTelegramListener();

        String[] propertyLines = new String[]{"javelin.call.tree.max=50000"};
        Telegram request = createRequestTelegram(propertyLines);

        // 期待値
        byte expectedTelegramKind = BYTE_TELEGRAM_KIND_UPDATE_PROPERTY;
        byte expectedRequestKind = BYTE_REQUEST_KIND_RESPONSE;
        Map<String, String> expectedConfigMap = createPattern1();
        expectedConfigMap.put("javelin.call.tree.max", "50000");

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertTelegram(expectedTelegramKind, expectedRequestKind, expectedConfigMap, response);
    }

    /**
     * @test サーバプロパティ設定電文-取得応答電文：パターン2(項目:4-1-4)
     * @condition 
     * @result
     */
    public void testReceiveTelegram_Update_Pattern2()
    {
        // 準備
        UpdateRequestTelegramListener telegramListener = new UpdateRequestTelegramListener();

        String[] propertyLines = new String[]{"javelin.log.args=true"};
        Telegram request = createRequestTelegram(propertyLines);

        // 期待値
        byte expectedTelegramKind = BYTE_TELEGRAM_KIND_UPDATE_PROPERTY;
        byte expectedRequestKind = BYTE_REQUEST_KIND_RESPONSE;
        Map<String, String> expectedConfigMap = createPattern1();
        expectedConfigMap.put("javelin.log.args", "true");

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertTelegram(expectedTelegramKind, expectedRequestKind, expectedConfigMap, response);
    }

    private void assertTelegram(byte expectedTelegramKind, byte expectedRequestKind,
            Map<String, String> expectedConfigMap, Telegram response)
    {
        TelegramAssertionUtil.assertHeader(expectedTelegramKind, expectedRequestKind,
                                           response.getObjHeader());

        // Bodyの検証
        for (Body body : response.getObjBody())
        {
            String value = expectedConfigMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail();
            }
            assertEquals(body.getStrObjName(), value, body.getStrItemName());
        }
    }

    private Telegram createRequestTelegram(String[] propertyLines)
    {
        Telegram request = new Telegram();
        Header header = new Header();
        header.setByteTelegramKind(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY);
        header.setByteRequestKind(BYTE_REQUEST_KIND_REQUEST);
        request.setObjHeader(header);

        List<Body> updatePropertyList = new ArrayList<Body>();

        for (String propertyLine : propertyLines)
        {
            String[] propertyLineArray = propertyLine.split("=");
            String updateProperty = propertyLineArray[0];
            String updateValue = propertyLineArray[1];

            if (updateValue == null || "".equals(updateValue))
            {
                continue;
            }

            Body addParam = new Body();
            addParam.setStrObjName(updateProperty);
            addParam.setStrItemName(updateValue);
            addParam.setObjItemValueArr(new Object[]{});

            updatePropertyList.add(addParam);
        }

        Body[] updatePropertyArray =
                                     updatePropertyList.toArray(new Body[updatePropertyList.size()]);

        request.setObjBody(updatePropertyArray);
        return request;
    }

    private Map<String, String> createPattern1()
    {
        return ConfigUpdater.getUpdatableConfig();
    }

    /**
     * [項番] 3-4-1 receiveTelegramのテスト。 <br />
     * ・プロパティ値を設定する。<br />
     * ・更新用のデータを電文に入力せず、receiveTelegramメソッドを実行する。<br />
     * →作成した電文が指定したものになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_NoRefresh()
        throws Exception
    {
        // 準備
        String resultItem[] =
                              {"0", "0", "false", "false", "false", "0", "false", "false", "0",
                                      "false", "true", "false", "1", "false", "true", "WARN", "0",
                                      "true", "0", "false", "true", "60000", "15", "false", "true",
                                      "100", "5.0", "false", "false", "false", "false", "true",
                                      "true", "true", "1000", "10", "10", "2000", "false", "10000",
                                      "100", "50", "true", "5000", "false", "60000", "true",
                                      "15000", "true", "true", "true", "true", "ERROR", "true",
                                      "5000", "true", "true", "true", "true", "false", "0",
                                      "false", "true", "102400", "true", "20", "false", "false",
                                      "true", "0"};

        Map<String, String> resultMap = makeResultMap(resultItem);

        String[] sendItem = {};
        String[] sendObjArray = {};

        Telegram sendTelegram = createTelegram(sendObjArray, sendItem);

        UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // 全てのBodyの検証
        for (Body body : receiveBody)
        {
            String value = resultMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail(body.getStrObjName() + " is not defined.");
            }
            assertEquals(body.getStrObjName(), value, body.getStrItemName());
        }

    }

    /**
     * [項番] 3-4-2 receiveTelegramのテスト。 <br />
     * ・プロパティ値を設定する。<br />
     * ・更新用のデータを電文に入力して、receiveTelegramメソッドを実行する。<br />
     * →作成した電文が指定したものになっている。<br />
     * 
     * @throws Exception 例外の発生
     */
    public void testReceiveTelegram_Refresh()
        throws Exception
    {
        // 準備

        String resultItem[] =
                              {"1000", "1000", "true", "true", "true", "1000", "true", "true",
                                      "1000", "true", "false", "true", "1000", "true", "false",
                                      "ERROR", "1000", "false", "0", "true", "false", "1000",
                                      "1000", "true", "false", "1000", "1000.0", "true", "true",
                                      "true", "true", "false", "false", "false", "1000", "1000",
                                      "1000", "1000", "true", "1000", "1000", "1000", "false",
                                      "1000", "true", "1000", "false", "1000", "false", "false",
                                      "false", "false", "WARN", "false", "1000", "false", "false",
                                      "false", "false", "true", "1000", "true", "false", "1000",
                                      "false", "1000", "true", "true", "false", "1000"};

        Map<String, String> resultMap = makeResultMap(resultItem);

        Telegram sendTelegram = createTelegram(resultObj_, resultItem);

        UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // 全てのBodyの検証
        for (Body body : receiveBody)
        {
            String value = resultMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail();
            }
            assertEquals(body.getStrObjName(), value, body.getStrItemName());
        }
    }

    /**
     * [項番] 3-4-3 receiveTelegramのテスト。 <br />
     * ・電文種別がサーバプロパティ更新通知以外の電文に対して、<br />
     * receiveTelegramメソッドを実行する。<br />
     * →作成した電文が全てnullになっている。<br />
     *
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_RequestKindOthers()
        throws Exception
    {
        byte[] requestKinds = {1, 2, 3, 4, 5, 6, 7, 9};

        for (byte requestKind : requestKinds)
        {
            // 準備
            Header sendHeader =
                                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                                requestKind);
            String[] inputDetail = {""};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, inputDetail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-4-4 receiveTelegramのテスト。 <br />
     * ・電文応答種別が要求以外の電文に対して、<br />
     * receiveTelegramメソッドを実行する。<br />
     * →作成した電文が全てnullになっている。<br />
     *
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_TelegramKindOthers()
        throws Exception
    {
        byte[] telegramKinds = {0, 2};

        for (byte telegramKind : telegramKinds)
        {
            // 準備
            Header sendHeader =
                                CreateTelegramUtil.createHeader(telegramKind,
                                                                BYTE_TELEGRAM_KIND_UPDATE_PROPERTY);
            String[] inputDetail = {""};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, inputDetail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-4-5 receiveTelegramのテスト。 <br />
     * ・プロパティ値を設定する。<br />
     * ・オブジェクト名がパラメータ名にない電文を作成し、receiveTelegramメソッドを実行する。<br />
     * →作成した電文が指定したものになっている。<br />
     * 
     * @throws Exception 例外の発生
     */
    public void testReceiveTelegram_ObjNotExist()
        throws Exception
    {
        // 準備
        String resultItem[] =
                              {"0", "0", "false", "false", "false", "0", "false", "false", "0",
                                      "false", "true", "false", "1", "false", "true", "WARN", "0",
                                      "true", "0", "false", "true", "60000", "15", "false", "true",
                                      "100", "5.0", "false", "false", "false", "false", "true",
                                      "true", "true", "1000", "10", "10", "2000", "false", "10000",
                                      "100", "50", "true", "5000", "false", "60000", "true",
                                      "15000", "true", "true", "true", "true", "ERROR", "true",
                                      "5000", "true", "true", "true", "true", "false", "0",
                                      "false", "true", "102400", "true", "20", "false", "false",
                                      "true", "0"};

        Map<String, String> resultMap = makeResultMap(resultItem);

        String[] sendObjArray = {"test"};
        String[] sendItemArray = {"1000"};

        Telegram sendTelegram = createTelegram(sendObjArray, sendItemArray);
        UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // 全てのBodyの検証
        for (Body body : receiveBody)
        {
            String value = resultMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail();
            }
            assertEquals(value, body.getStrItemName());
        }
    }

    /**
     * [項番] 3-4-6 receiveTelegramのテスト。 <br />
     * ・プロパティ値を設定する。<br />
     * ・項目名(int)に不正な値(String)を入力した電文を作成し、<br />
     *  receiveTelegramメソッドを実行する。<br />
     * →作成した電文が指定したものになっている。<br />
     * 
     * システムログの目視の必要あり。
     * 
     * @throws Exception 例外の発生
     */
    public void testReceiveTelegram_itemName_IntToString()
        throws Exception
    {
        // 準備
        String resultItem[] =
                              {"0", "0", "false", "false", "false", "0", "false", "false", "0",
                                      "false", "true", "false", "1", "false", "true", "WARN", "0",
                                      "true", "0", "false", "true", "60000", "15", "false", "true",
                                      "100", "5.0", "false", "false", "false", "false", "true",
                                      "true", "true", "1000", "10", "10", "2000", "false", "10000",
                                      "100", "50", "true", "5000", "false", "60000", "true",
                                      "15000", "true", "true", "true", "true", "ERROR", "true",
                                      "5000", "true", "true", "true", "true", "false", "0",
                                      "false", "true", "102400", "true", "20", "false", "false",
                                      "true", "0"};

        Map<String, String> resultMap = makeResultMap(resultItem);

        String[] sendObjArray = {"javelin.alarmThreshold"};
        String[] sendItemArray = {"test"};

        Telegram sendTelegram = createTelegram(sendObjArray, sendItemArray);

        UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // 全てのBodyの検証
        for (Body body : receiveBody)
        {
            String value = resultMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail();
            }
            assertEquals(value, body.getStrItemName());
        }
    }

    /**
     * [項番] 3-4-7 receiveTelegramのテスト。 <br />
     * ・プロパティ値を設定する。<br />
     * ・項目名(boolean)に不正な値(String)を入力した電文を作成し、<br />
     *  receiveTelegramメソッドを実行する。<br />
     * →作成した電文が指定したものになっている。<br />
     * 
     * システムログの目視の必要あり。
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_itemName_BooleanToString()
        throws Exception
    {
        String resultItem[] =
                              {"0", "0", "false", "false", "false", "0", "false", "false", "0",
                                      "false", "true", "false", "1", "false", "true", "WARN", "0",
                                      "true", "0", "false", "true", "60000", "15", "false", "true",
                                      "100", "5.0", "false", "false", "false", "false", "true",
                                      "true", "true", "1000", "10", "10", "2000", "false", "10000",
                                      "100", "50", "true", "5000", "false", "60000", "true",
                                      "15000", "true", "true", "true", "true", "ERROR", "true",
                                      "5000", "true", "true", "true", "true", "false", "0",
                                      "false", "true", "102400", "true", "20", "false", "false",
                                      "true", "0"};

        // 準備
        Map<String, String> resultMap = makeResultMap(resultItem);

        String[] sendObjArray = {"javelin.jdbc.oracle.allowSqlTrace"};
        String[] sendItemArray = {"test"};

        Telegram sendTelegram = createTelegram(sendObjArray, sendItemArray);

        UpdateRequestTelegramListener listener = new UpdateRequestTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_UPDATE_PROPERTY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // 全てのBodyの検証
        for (Body body : receiveBody)
        {
            String value = resultMap.get(body.getStrObjName());
            // 予期したパラメータが入っていないときには、検証に失敗。
            if (value == null)
            {
                fail();
            }
            assertEquals(value, body.getStrItemName());
        }
    }

    /**
     * 結果のMapを作成する。
     * @param resultItem 項目名の結果
     * @return 結果を保存したMap
     */
    private synchronized Map<String, String> makeResultMap(final String[] resultItem)
    {
        Map<String, String> resultMap = new HashMap<String, String>();

        //　検証用データをMapに保存する。
        for (int num = 0; num < resultObj_.length; num++)
        {
            resultMap.put(resultObj_[num], resultItem[num]);
        }
        return resultMap;
    }

    /**
     * 電文を作成する。
     * @param sendItemArray サーバが受信する項目名の配列
     * @return 電文
     */
    private Telegram createTelegram(final String[] sendObjArray, final String[] sendItemArray)
    {
        Header sendHeader =
                            CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                            BYTE_TELEGRAM_KIND_UPDATE_PROPERTY);

        // 遅延更新は行わない
        Long[] inputDetail = {};

        Telegram sendTelegram = null;

        // 送信する項目の長さが0の場合と0でない場合で処理を分ける。
        if (sendItemArray.length == 0)
        {
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, inputDetail);
            Body[] sendBodies = {sendBody};
            sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        }
        else
        {
            Body[] sendBodies = new Body[sendItemArray.length];
            for (int num = 0; num < sendItemArray.length; num++)
            {
                sendBodies[num] =
                                  CreateTelegramUtil.createBodyValue(sendObjArray[num],
                                                                     sendItemArray[num],
                                                                     ItemType.ITEMTYPE_SHORT, 0, inputDetail);
            }
            sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        }
        return sendTelegram;
    }
}
