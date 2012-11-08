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
import junit.framework.TestCase;

/**
 * JVNログファイル一覧取得のテストクラス。
 * @author fujii
 *
 */
public class JvnLogListTelegramListenerTest extends TestCase implements TelegramConstants
{
    private static final String BASE_DIR = "./src/test/resources/telegram";
    
    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp()
    {
        // オプションファイルから、オプション設定を読み込む。
        this.config_ = new JavelinConfig(BASE_DIR + "/conf");
        this.config_.setJavelinFileDir(BASE_DIR);
        SystemLogger.initSystemLog(this.config_);
    }

    /**
     * [項番] 3-2-4 receiveTelegramのテスト。 <br />
     * ・ファイル数が1のフォルダに対するログファイル一覧電文の取得。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_FileNum1()
    {
        this.config_.setJavelinFileDir(BASE_DIR + "/fileNum1");

        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {"file1.jvn"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 1, detail1,
                                  receiveBody[0]);
    }

    /**
     * [項番] 3-2-5 receiveTelegramのテスト。 <br />
     * ・ファイル数が3のフォルダに対するログファイル一覧電文の取得。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_FileNum3()
    {
        this.config_.setJavelinFileDir(BASE_DIR + "/fileNum3");

        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {"file1.jvn", "file2.jvn", "file3.jvn"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 3, detail1,
                                  receiveBody[0]);
    }

    /**
     * [項番] 3-2-6 receiveTelegramのテスト。 <br />
     * ・Jvnファイルが2つ、それ以外のファイルが2つ入っているフォルダ
     *  に対するログファイル一覧電文の取得。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_JvnFileNum2_OtherFileNum2()
    {
        this.config_.setJavelinFileDir(BASE_DIR + "/jvn2_other2");

        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {"file1.jvn", "file2.jvn"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 2, detail1,
                                  receiveBody[0]);
    }

    /**
     * [項番] 3-2-7 receiveTelegramのテスト。 <br />
     * ・Jvnファイルが入っていないフォルダに対するログファイル一覧電文の取得。<br />
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_OtherFiles()
    {
        this.config_.setJavelinFileDir(BASE_DIR + "/otherFiles");

        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 0, detail1,
                                  receiveBody[0]);
    }

    /**
     * [項番] 3-2-8 receiveTelegramのテスト。 <br />
     * ・ファイルが空のフォルダに対するログファイル一覧電文の取得。<br />
     * →作成した電文が指定したものになっている。<br />
     */
    public void testReceiveTelegram_FileNum0()
    {
        this.config_.setJavelinFileDir(BASE_DIR + "/fileNum0");

        // 準備
        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        Header receiveHeader = receiveTelegram.getObjHeader();
        Body[] receiveBody = receiveTelegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, receiveHeader.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, receiveHeader.getByteRequestKind());

        String[] detail1 = {};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 0, detail1,
                                  receiveBody[0]);
    }

    /**
     * [項番] 3-2-9 receiveTelegramのテスト。 <br />
     * ・電文種別がログファイル一覧取得でないときに、
     *  receiveTeleramメソッドを呼ぶ。
     * →作成した電文が全てnullになっている。<br />
     */
    public void testReceiveTelegram_RequestKindOthers()
    {
        // 準備
        byte[] requestKinds = {1, 2, 3, 4, 5, 6, 7, 8};

        for (byte requestKind : requestKinds)
        {

            Header sendHeader =
                    CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST, requestKind);
            Object[] detail = {new Object()};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-2-10 receiveTelegramのテスト。 <br />
     * ・電文応答種別が要求でないときに、
     *  receiveTeleramメソッドを呼ぶ。
     * →作成した電文が全てnullになっている。<br />
     */
    public void testReceiveTelegram_TelegramKindOthers()
    {
        // 準備
        byte[] telegramKinds = {0, 2};

        for (byte telegramKind : telegramKinds)
        {

            Header sendHeader =
                    CreateTelegramUtil.createHeader(telegramKind, BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
            Object[] detail = {new Object()};
            Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

            Body[] sendBodies = {sendBody};
            Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
            JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

            // 実行
            Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

            // 検証
            assertNull(receiveTelegram);
        }
    }

    /**
     * [項番] 3-2-11 receiveTelegramのテスト。 <br />
     * ・存在しないフォルダに対して、receiveTeleramメソッドを呼ぶ。
     * →作成した電文がnullになっている。<br />
     */
    public void testReceiveTelegram_FolderNotExist()
    {
        // 準備
        this.config_.setJavelinFileDir(BASE_DIR + "/FolderNotExist");

        Header sendHeader =
                CreateTelegramUtil.createHeader(BYTE_REQUEST_KIND_REQUEST,
                                                BYTE_TELEGRAM_KIND_JVN_FILE_LIST);
        Object[] detail = {new Object()};
        Body sendBody = CreateTelegramUtil.createBodyValue("", "", ItemType.ITEMTYPE_SHORT, 0, detail);

        Body[] sendBodies = {sendBody};
        Telegram sendTelegram = CreateTelegramUtil.createTelegram(sendHeader, sendBodies);
        JvnLogListTelegramListener listener = new JvnLogListTelegramListener();

        // 実行
        Telegram receiveTelegram = listener.receiveTelegram(sendTelegram);

        // 検証
        assertNull(receiveTelegram);
    }

}
