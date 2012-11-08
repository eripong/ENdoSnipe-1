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

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.AssertUtil;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.CreateTelegramUtil;
import jp.co.acroquest.test.util.JavelinTestUtil;
import junit.framework.TestCase;

/**
 * JVNログダウンロード機能のテストコード
 * @author fujii
 *
 */
public class JvnLogDownloadTelegramListenerTest extends TestCase implements TelegramConstants
{
    private static final String CONF_PATH = "/telegram/conf/javelin.properties";
    private static final String LOGS_PATH = "/telegram/logs";
    
    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        // オプションファイルから、オプション設定を読み込む。
        JavelinTestUtil.camouflageJavelinConfig(getClass(), CONF_PATH);
        String logDir = JavelinTestUtil.getAbsolutePath(getClass(), LOGS_PATH);
        this.config_ = new JavelinConfig();
        this.config_.setJavelinFileDir(logDir);
        SystemLogger.initSystemLog(this.config_);
    }

    /**
     * [項番] 3-1-5 receiveTelegramのテスト。 <br />
     * ・JVNログダウンロード取得用の電文を受信する。
     * →作成した電文が指定したものになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_FileNum1()
        throws Exception
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE);
        String[] inputDetail = {"file1.jvn"};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("jvnFile", "jvnFileName", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {"file1.jvn"};
        String[] detail2 = {"contentOfFirstJavelinFile"};

        // 1番目のBodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 1, detail1,
                                  receiveBody[0]);
        AssertUtil.assertTelegram("jvnFile", "jvnFileContent", BYTE_ITEMMODE_KIND_STRING, 1,
                                  detail2, receiveBody[1]);
    }

    /**
     * [項番] 3-1-6 receiveTelegramのテスト。 <br />
     * ・電文種別をJVNログ出力通知以外{1,2,3,4,5,7,8,9}にして、
     *  JVNログダウンロード取得用の電文を受信する。
     * →作成した電文が全てnullになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_RequestKindOthers()
        throws Exception
    {
        byte[] requestKinds = {1, 2, 3, 4, 5, 7, 8, 9};

        for (byte requestKind : requestKinds)
        {

            // 準備
            Header sendHeader =
                    CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST, requestKind);
            String[] inputDetail = {"file1.jvn"};
            Body sendBody =
                    CreateTelegramUtil.createBodyValue("jvnFile", "jvnFileName", ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-1-7 receiveTelegramのテスト。 <br />
     * ・電文応答種別を要求以外{0,2}にして、
     *  JVNログダウンロード取得用の電文を受信する。
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
                    CreateTelegramUtil.createHeader(telegramKind, BYTE_TELEGRAM_KIND_JVN_FILE);
            String[] inputDetail = {"file1.jvn"};
            Body sendBody =
                    CreateTelegramUtil.createBodyValue("jvnFile", "jvnFileName", ItemType.ITEMTYPE_SHORT, 0,
                                                       inputDetail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-1-8 receiveTelegramのテスト。 <br />
     * ・オブジェクト名を"test"にして、JVNログダウンロード取得用の電文を受信する。
     * →作成した電文がnullになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_ObjNameOther()
        throws Exception
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE);
        String[] inputDetail = {"file1.jvn"};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("test", "jvnFileName", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        assertNull(receiveTelegram);
    }

    /**
     * [項番] 3-1-9 receiveTelegramのテスト。 <br />
     * ・項目名を"testItem"にして、JVNログダウンロード取得用の電文を受信する。
     * →作成した電文がnullになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_ItemNameOther()
        throws Exception
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE);
        String[] inputDetail = {"file1.jvn"};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("jvnFile", "testItem", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        assertNull(receiveTelegram);
    }

    /**
     * [項番] 3-1-10 receiveTelegramのテスト。 <br />
     * ・詳細を空にして、JVNログダウンロード取得用の電文を受信する。
     * →作成した電文がnullになっている。<br />
     * 
     * @throws Exception 例外が発生した場合
     */
    public void testReceiveTelegram_DetailEmpty()
        throws Exception
    {
        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE);
        String[] inputDetail = {};
        Body sendBody =
                CreateTelegramUtil.createBodyValue("jvnFile", "jvnFileName", ItemType.ITEMTYPE_SHORT, 0,
                                                   inputDetail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogDownloadTelegramListener listener = new JvnLogDownloadTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        assertNull(receiveTelegram);
    }

}
