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

import java.util.ArrayList;
import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.RequestBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.communicator.util.TelegramAssertionUtil;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.communicate.JavelinTelegramCreator;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.AssertUtil;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.CreateTelegramUtil;
import junit.framework.TestCase;

/**
 * システムリソース取得のテストクラス。
 * @author fujii
 */
public class SystemResourceTelegramListenerTest extends TestCase implements TelegramConstants
{
    private static final String BASE_DIR = ".";
    
    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    /**
     * @test リソース通知応答電文-リソース通知要求外電文の処理(項目:2-1-1)
     * @condition 
     * @result
     */
    public void testCreate_NotResource()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        List<Long> accumulatedTimes = null;
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_GET;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        Telegram resourceTelegram = telegramListener.receiveTelegram(telegram);

        // 検証
        assertNull(resourceTelegram);
    }

    /**
     * @test リソース通知応答電文-オブジェクト単数(項目:2-1-2)
     * @condition 
     * @result
     */
    public void testCreate_SingleItem()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, ITEMNAME_PROCESS_CPU_TOTAL_TIME);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames = new String[]{ITEMNAME_PROCESS_CPU_TOTAL_TIME};
        byte[] expectedItemModes = new byte[]{BYTE_ITEMMODE_KIND_8BYTE_INT};
        int[] expectedLoopCounts = new int[]{1};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);

    }

    /**
     * @test リソース通知応答電文-オブジェクト複数(項目:2-1-3)
     * @condition 
     * @result
     */
    public void testCreate_MultiItem()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request =
                createRequestTelegram(telegramKind, requestKind, ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                                      ITEMNAME_JAVAUPTIME, ITEMNAME_JAVAPROCESS_THREAD_TOTAL_COUNT);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames =
                new String[]{ITEMNAME_PROCESS_CPU_TOTAL_TIME, ITEMNAME_JAVAUPTIME, ITEMNAME_JAVAPROCESS_THREAD_TOTAL_COUNT};
        byte[] expectedItemModes =
                new byte[]{BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_4BYTE_INT};
        int[] expectedLoopCounts = new int[]{1, 1, 1};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);
    }

    /**
     * @test リソース通知応答電文-全オブジェクト(項目:2-1-4)
     * @condition 
     * @result
     */
    public void testCreate_AllItem()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        String[] itemNames =
                new String[]{TelegramConstants.ITEMNAME_ACQUIREDTIME, 
                TelegramConstants.ITEMNAME_PROCESS_CPU_TOTAL_TIME, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_COMMIT, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_USED, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_MAX, 
                TelegramConstants.ITEMNAME_JAVAUPTIME, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_COMMIT, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_USED, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_MAX, 
                TelegramConstants.ITEMNAME_PROCESS_MEMORY_PHYSICAL_MAX,
                TelegramConstants.ITEMNAME_PROCESS_MEMORY_PHYSICAL_FREE,
                TelegramConstants.ITEMNAME_SYSTEM_CPU_PROCESSOR_COUNT,
                TelegramConstants.ITEMNAME_SYSTEM_MEMORY_SWAP_MAX,
                TelegramConstants.ITEMNAME_SYSTEM_MEMORY_SWAP_FREE, 
                TelegramConstants.ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_MAX, 
                TelegramConstants.ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_FREE,
                TelegramConstants.ITEMNAME_NETWORKINPUTSIZEOFPROCESS,
                TelegramConstants.ITEMNAME_NETWORKOUTPUTSIZEOFPROCESS,
                TelegramConstants.ITEMNAME_FILEINPUTSIZEOFPROCESS, 
                TelegramConstants.ITEMNAME_FILEOUTPUTSIZEOFPROCESS, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_THREAD_TOTAL_COUNT, 
                TelegramConstants.ITEMNAME_JAVAPROCESS_GC_TIME_TOTAL};

        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, itemNames);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames = itemNames;
        byte[] expectedItemModes =
                new byte[]{BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_4BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT, BYTE_ITEMMODE_KIND_8BYTE_INT,
                        BYTE_ITEMMODE_KIND_4BYTE_INT,
                        BYTE_ITEMMODE_KIND_8BYTE_INT,};
        int[] expectedLoopCounts =
                new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);
    }

    /**
     * @test リソース通知応答電文-initialize指定：他オブジェクト無し(項目:2-1-5)
     * @condition 
     * @result
     */
    public void testCreate_InitializeOnly()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, ITEMNAME_INITIALIZE);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        TelegramAssertionUtil.assertHeader(expectedTelegramKind, expectedRequestKind,
                                           response.getObjHeader());
        assertEquals(0, response.getObjBody().length);
    }

    /**
     * @test リソース通知応答電文-initialize指定：他オブジェクト有り(項目:2-1-6)
     * @condition 
     * @result
     */
    public void testCreate_Initialize()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request =
                createRequestTelegram(telegramKind, requestKind, ITEMNAME_INITIALIZE,
                                      ITEMNAME_CALLTREECOUNT, ITEMNAME_JAVAPROCESS_CLASSLOADER_CLASS_CURRENT);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        TelegramAssertionUtil.assertHeader(expectedTelegramKind, expectedRequestKind,
                                           response.getObjHeader());
        assertEquals(0, response.getObjBody().length);
    }

    /**
     * @test リソース通知応答電文-系列データ設定：なし(項目:2-1-6)
     * @condition 
     * @result
     */
    public void testCreate_NoSeries()
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames = new String[]{ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT + "-name"};
        byte[] expectedItemModes =
                new byte[]{BYTE_ITEMMODE_KIND_4BYTE_INT, BYTE_ITEMMODE_KIND_STRING};
        int[] expectedLoopCounts = new int[]{0, 0};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);
    }

    /**
     * @test リソース通知応答電文-系列データ設定：単数(項目:2-1-7)
     * @condition 
     * @result
     */
    public void testCreate_SingleSeries() throws Exception
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT);

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames = new String[]{ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT + "-name"};
        byte[] expectedItemModes =
                new byte[]{BYTE_ITEMMODE_KIND_4BYTE_INT, BYTE_ITEMMODE_KIND_STRING};
        int[] expectedLoopCounts = new int[]{1, 1};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);
    }

    /**
     * @test リソース通知応答電文-系列データ設定：複数(項目:2-1-8)
     * @condition 
     * @result
     */
    public void testCreate_Multieries() throws Exception
    {
        // 準備
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_RESOURCENOTIFY;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
        SystemResourceTelegramListener telegramListener = new SystemResourceTelegramListener();
        Telegram request = createRequestTelegram(telegramKind, requestKind, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT);
        List<String> largeList = createLargeList();
        List<String> largeList2 = createLargeList();

        // 期待値
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;
        String[] expectedItemNames = new String[]{ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT, ITEMNAME_JAVAPROCESS_COLLECTION_LIST_COUNT + "-name"};
        byte[] expectedItemModes =
                new byte[]{BYTE_ITEMMODE_KIND_4BYTE_INT, BYTE_ITEMMODE_KIND_STRING};
        int[] expectedLoopCounts = new int[]{2, 2};

        // 実施
        Telegram response = telegramListener.receiveTelegram(request);

        // 検証
        assertResourceTelegram(expectedTelegramKind, expectedRequestKind, expectedItemNames,
                               expectedItemModes, expectedLoopCounts, response);
    }

    private List<String> createLargeList()
    {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < 60000; index++)
        {
            list.add(String.valueOf(index));
        }
        return list;
    }

    private void assertResourceTelegram(byte expectedTelegramKind, byte expectedRequestKind,
            String[] itemNames, byte[] expectedItemModes, int[] loopCounts, Telegram response)
    {
        // ヘッダの検証
        TelegramAssertionUtil.assertHeader(expectedTelegramKind, expectedRequestKind,
                                           response.getObjHeader());

        // Bodyの検証
        int bodyIndex = 0;
        TelegramAssertionUtil.assertResourceTelegram(
                                                     TelegramConstants.TIME_RESOURCE,
                                                     TelegramConstants.ITEMNAME_TIME,
                                                     TelegramConstants.BYTE_ITEMMODE_KIND_8BYTE_INT,
                                                     1, response.getObjBody()[bodyIndex++]);

        for (int index = 0; index < itemNames.length; index++)
        {
            TelegramAssertionUtil.assertResourceTelegram(TelegramConstants.OBJECTNAME_RESOURCE,
                                                         itemNames[index],
                                                         expectedItemModes[index],
                                                         loopCounts[index],
                                                         response.getObjBody()[bodyIndex++]);
        }

        assertEquals(bodyIndex, response.getObjBody().length);
    }

    private Telegram createRequestTelegram(byte telegramKind, byte requestKind, String... itemNames)
    {
        Header header = new Header();
        header.setByteTelegramKind(telegramKind);
        header.setByteRequestKind(requestKind);

        Body[] bodies = new Body[itemNames.length];

        for (int index = 0; index < itemNames.length; index++)
        {
            bodies[index] = makeResourceRequestBody(itemNames[index]);
        }

        Telegram request = new Telegram();
        request.setObjHeader(header);
        request.setObjBody(bodies);

        return request;
    }

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。<br />
     */
    @Override
    public void setUp()
    {
        // オプションファイルから、オプション設定を読み込む。
        this.config_ = new JavelinConfig(BASE_DIR + "/lib");
        this.config_.setJavelinFileDir(BASE_DIR);
        SystemLogger.initSystemLog(this.config_);
    }

    /**
     * [項番] 3-5-1 ～ 3-5-17, 3-5-22, 3-5-23<br />
     * receiveTelegramのテスト。 <br />
     * ・システムリソースの取得。<br />
     * →作成した電文が指定したものになっている。<br />
     * →各システムリソースがnullでない。<br />
     */
    public void testReceiveTelegram_ResourceGet()
    {
        // 準備
        String[] resultItem =
                              new String[]{
                                      TelegramConstants.ITEMNAME_ACQUIREDTIME,
                                      TelegramConstants.ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                                      TelegramConstants.ITEMNAME_JAVAUPTIME,
                                      TelegramConstants.ITEMNAME_SYSTEM_CPU_PROCESSOR_COUNT,
                                      TelegramConstants.ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_MAX,
                                      TelegramConstants.ITEMNAME_PROCESS_MEMORY_VIRTUALMACHINE_FREE,
                                      TelegramConstants.ITEMNAME_PROCESS_MEMORY_PHYSICAL_MAX,
                                      TelegramConstants.ITEMNAME_PROCESS_MEMORY_PHYSICAL_FREE,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_COMMIT,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_USED,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_HEAP_MAX,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_COMMIT,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_USED,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_MEMORY_NONHEAP_MAX,
                                      TelegramConstants.ITEMNAME_SYSTEM_MEMORY_SWAP_MAX,
                                      TelegramConstants.ITEMNAME_SYSTEM_MEMORY_SWAP_FREE,
                                      TelegramConstants.ITEMNAME_PROCESS_MEMORY_VIRTUAL_USED,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_THREAD_TOTAL_COUNT,
                                      TelegramConstants.ITEMNAME_JAVAPROCESS_GC_TIME_TOTAL};
        
        Byte[] resultItemKind = {3, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 2, 3};

        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[resultItem.length];
        for (int num = 0; num < resultItem.length; num++)
        {
            String[] inputDetail = {""};
            sendBodies[num] =
                    CreateTelegramUtil.createBodyValue("resources", resultItem[num], ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);
        }

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        for (int num = 1; num < receiveBody.length; num++)
        {
            AssertUtil.assertResourceTelegram("resources", resultItem[num - 1], resultItemKind[num - 1], 1,
                                              receiveBody[num]);
            assertNotNull(receiveBody[num].getObjItemValueArr());
        }
    }

    /**
     * [項番] 3-5-18, 3-5-19<br />
     * receiveTelegramのテスト。 <br />
     * ・ネットワーク受信量を1、ネットワーク送信量を2に設定して、<br />
     *  receiveTelegramメソッドを利用して、ネットワーク受信量、送信量を取得する。<br />
     * →作成した電文が指定したものになっている。<br />
     * →ネットワーク受信量が1、ネットワーク送信量が2になっている。
     */
    public void testReceiveTelegram_NetWork()
    {
        // 準備
        String[] resultItem = {TelegramConstants.ITEMNAME_NETWORKINPUTSIZEOFPROCESS,
                TelegramConstants.ITEMNAME_NETWORKOUTPUTSIZEOFPROCESS};

        Byte[] resultItemKind = {3, 3};

        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[resultItem.length];
        for (int num = 0; num < resultItem.length; num++)
        {
            String[] inputDetail = {""};
            sendBodies[num] =
                    CreateTelegramUtil.createBodyValue("resources", resultItem[num], ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);
        }

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        Long[] inputDetail1 = {(long)1};
        Long[] inputDetail2 = {(long)2};

        AssertUtil.assertTelegram("resources", resultItem[0], resultItemKind[0], 1, inputDetail1,
                                  receiveBody[1]);
        AssertUtil.assertTelegram("resources", resultItem[1], resultItemKind[1], 1, inputDetail2,
                                  receiveBody[2]);
    }

    /**
     * [項番] 3-5-20, 3-5-21<br />
     * receiveTelegramのテスト。 <br />
     * ・ファイル入力量を3、ファイル出力量を4に設定して、<br />
     *  receiveTelegramメソッドを利用して、ファイル入力量、出力量を取得する。<br />
     * →作成した電文が指定したものになっている。<br />
     * →ファイル入力量が3、ファイル出力量が4になっている。
     */
    public void testReceiveTelegram_File()
    {
        // 準備
        String[] resultItem = {TelegramConstants.ITEMNAME_FILEINPUTSIZEOFPROCESS,
                TelegramConstants.ITEMNAME_FILEOUTPUTSIZEOFPROCESS};

        Byte[] resultItemKind = {3, 3};

        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[resultItem.length];
        for (int num = 0; num < resultItem.length; num++)
        {
            String[] inputDetail = {""};
            sendBodies[num] =
                    CreateTelegramUtil.createBodyValue("resources", resultItem[num], ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);
        }

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        Long[] inputDetail1 = {(long)3};
        Long[] inputDetail2 = {(long)4};

        AssertUtil.assertTelegram("resources", resultItem[0], resultItemKind[0], 1, inputDetail1,
                                  receiveBody[1]);
        AssertUtil.assertTelegram("resources", resultItem[1], resultItemKind[1], 1, inputDetail2,
                                  receiveBody[2]);
    }

    /**
     * [項番] 3-5-24<br />
     * receiveTelegramのテスト。 <br />
     * ・無効なBodyと有効なBodyを持つ電文を作成し、receiveTelegramメソッドを呼ぶ。<br />
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_OneInvalid_OneValidBody()
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[2];
        String[] inputDetail = {""};
        sendBodies[0] =
                CreateTelegramUtil.createBodyValue("resources", "testItem", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);
        sendBodies[1] =
                CreateTelegramUtil.createBodyValue("resources", "acquiredTime", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        AssertUtil.assertResourceTelegram("resources", "acquiredTime", (byte)3, 1, receiveBody[1]);
        assertNotNull(receiveBody[0].getObjItemValueArr());
    }

    /**
     * [項番] 3-5-25<br />
     * receiveTelegramのテスト。 <br />
     * ・有効なBody、無効なBody、有効なBodyを持つ電文を作成し、receiveTelegramメソッドを呼ぶ。<br />
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_TwoValid_OneInvalidBody()
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[3];
        String[] inputDetail = {""};
        sendBodies[0] =
                CreateTelegramUtil.createBodyValue("resources", TelegramConstants.ITEMNAME_ACQUIREDTIME, ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);
        sendBodies[1] =
                CreateTelegramUtil.createBodyValue("testObj", "testItem", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);
        sendBodies[2] =
                CreateTelegramUtil.createBodyValue(
                                                           "resources",
                                                           TelegramConstants.ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                                                           ItemType.ITEMTYPE_SHORT, 0, inputDetail);

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        AssertUtil.assertResourceTelegram("resources", TelegramConstants.ITEMNAME_ACQUIREDTIME,
                                          (byte)3, 1, receiveBody[1]);
        assertNotNull(receiveBody[0].getObjItemValueArr());
        AssertUtil.assertResourceTelegram("resources",
                                          TelegramConstants.ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                                          (byte)3, 1, receiveBody[2]);
        assertNotNull(receiveBody[1].getObjItemValueArr());
    }

    /**
     * [項番] 3-5-26<br />
     * receiveTelegramのテスト。 <br />
     * ・有効なBodyを3つ持つ電文を作成し、receiveTelegramメソッドを呼ぶ。<br />
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_ThreeValidBody()
    {
        // 準備
        String[] resultItem =
                              {TelegramConstants.ITEMNAME_ACQUIREDTIME,
                                      TelegramConstants.ITEMNAME_PROCESS_CPU_TOTAL_TIME,
                                      TelegramConstants.ITEMNAME_JAVAUPTIME,};

        Byte[] resultItemKind = {3, 3, 3};

        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Body[] sendBodies = new Body[resultItem.length];
        for (int num = 0; num < resultItem.length; num++)
        {
            String[] inputDetail = {""};
            sendBodies[num] =
                    CreateTelegramUtil.createBodyValue("resources", resultItem[num], ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);
        }

        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        // Bodyの検証
        for (int num = 1; num < receiveBody.length; num++)
        {
            AssertUtil.assertResourceTelegram("resources", resultItem[num - 1],
                                              resultItemKind[num - 1], 1, receiveBody[num]);
            assertNotNull(receiveBody[num].getObjItemValueArr());
        }
    }

    /**
     * [項番] 3-5-27 receiveTelegramのテスト。 <br />
     * ・電文種別がリソース通知でないときに、receiveTeleramメソッドを呼ぶ。<br />
     * →作成した電文が全てnullになっている。<br />
     */
    public void testReceiveTelegram_RequestKindOthers()
    {
        // 準備
        byte[] requestKinds = {1, 2, 4, 5, 6, 7, 8, 9};

        for (byte requestKind : requestKinds)
        {

            Header sendHeader =
                    CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST, requestKind);
            Object[] detail = {new Object()};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-5-28 receiveTelegramのテスト。 <br />
     * ・電文応答種別が要求でないときに、receiveTeleramメソッドを呼ぶ。<br />
     * →作成した電文が全てnullになっている。<br />
     */
    public void testReceiveTelegram_TelegramKindOthers()
    {
        // 準備
        byte[] telegramKinds = {0, 2};

        for (byte telegramKind : telegramKinds)
        {

            Header sendHeader =
                    CreateTelegramUtil.createHeader(telegramKind, BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
            Object[] detail = {new Object()};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-5-29 receiveTelegramのテスト。 <br />
     * ・オブジェクト名が"resources"でないとき、receiveTeleramメソッドを呼ぶ。<br />
     * →作成した電文のBodyが空である。<br />
     */
    public void testReceiveTelegram_ObjNameOther()
    {
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Object[] detail = {new Object()};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("test", "acquiredTime", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        assertEquals(1, receiveBody.length);
    }

    /**
     * [項番] 3-5-30 receiveTelegramのテスト。 <br />
     * ・項目名が存在しない項目名のとき、receiveTeleramメソッドを呼ぶ。<br />
     * →作成した電文のBodyが空である。<br />
     */
    public void testReceiveTelegram_ItemNameOther()
    {
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_RESOURCENOTIFY);
        Object[] detail = {new Object()};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("resources", "test", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        SystemResourceTelegramListener listener = new SystemResourceTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_RESOURCENOTIFY, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        assertEquals(1, receiveBody.length);
    }

    /**
     * 要求本体を作成する。
     *
     * @param itemName 項目名
     * @return 要求本体
     */
    private RequestBody makeResourceRequestBody(final String itemName)
    {
        RequestBody requestBody = new RequestBody();
        requestBody.setStrObjName(OBJECTNAME_RESOURCE);
        requestBody.setStrItemName(itemName);
        return requestBody;
    }
}
