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

import jp.co.dgic.testing.common.virtualmock.MockObjectManager;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.communicator.TelegramCreator;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.communicate.telegram.util.AssertUtil;
import jp.co.acroquest.test.util.JavelinTestUtil;
import junit.framework.TestCase;

/**
 * TelegramUtilのテストコード
 * @author fujii
 *
 */
public class TelegramUtilTest extends TestCase implements TelegramConstants
{

    private static final String CONFIG_PATH = "/telegram/conf/javelin.properties";

    /** Javelinの設定ファイル */
    private JavelinConfig config_;

    /**
     * 初期化メソッド<br />
     * システムログの初期化を行う。
     */
    @Override
    public void setUp() throws Exception
    {
        // オプションファイルから、オプション設定を読み込む。
        MockObjectManager.initialize();
        JavelinTestUtil.camouflageJavelinConfig(getClass(), CONFIG_PATH);
        this.config_ = new JavelinConfig();
        this.config_.setJavelinFileDir(JavelinTestUtil.getAbsolutePath(getClass(), "/telegram/logs"));
        SystemLogger.initSystemLog(this.config_);
    }

    /**
     * [項番] 3-1-1 createJvnLogDownloadTelegramのテスト。 <br />
     * ・"file1.jvn"に対して、ダウンロード用ログ電文を作成する。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogDownloadTelegram_FileNum1()
    {
        // 準備
        String[] jvnFileNames = {"file1.jvn"};

        // 実行
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                             jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail1 = {"file1.jvn"};
        String[] detail2 = {"contentOfFirstJavelinFile"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 1, detail1,
                                  body[0]);
        AssertUtil.assertTelegram("jvnFile", "jvnFileContent", BYTE_ITEMMODE_KIND_STRING, 1,
                                  detail2, body[1]);
    }

    /**
     * [項番] 3-1-2 createJvnLogDownloadTelegramのテスト。 <br />
     * ・"file1.jvn","file2.jvn","file3.jvn"に対して、ダウンロード用ログ電文を作成する。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogDownloadTelegram_FileNum3()
    {
        // 準備
        String[] jvnFileNames = {"file1.jvn", "file2.jvn", "file3.jvn"};

        // 実行
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                             jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail1 = {"file1.jvn", "file2.jvn", "file3.jvn"};
        String[] detail2 =
                {"contentOfFirstJavelinFile", "contentOfSecondJavelinFile",
                        "contentOfThirdJavelinFile"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 3, detail1,
                                  body[0]);
        AssertUtil.assertTelegram("jvnFile", "jvnFileContent", BYTE_ITEMMODE_KIND_STRING, 3,
                                  detail2, body[1]);
    }

    /**
     * [項番] 3-1-3 createJvnLogDownloadTelegramのテスト。 <br />
     * ・存在しないファイル"nofile1.jvn"と,存在するファイル"file1.jvn"に対して、
     * ダウンロード用ログ電文を作成する。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogDownloadTelegram_NotExistFileAndExistFile()
    {
        // 準備
        String[] jvnFileNames = {"nofile1.jvn", "file1.jvn"};

        // 実行
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                             jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail1 = {"nofile1.jvn", "file1.jvn"};
        String[] detail2 = {"", "contentOfFirstJavelinFile"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 2, detail1,
                                  body[0]);
        AssertUtil.assertTelegram("jvnFile", "jvnFileContent", BYTE_ITEMMODE_KIND_STRING, 2,
                                  detail2, body[1]);
    }

    /**
     * [項番] 3-1-4 createJvnLogDownloadTelegramのテスト。 <br />
     * ・存在しないファイル"nofile1.jvn"に対して、ダウンロード用ログ電文を作成する。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogDownloadTelegram_NotExist()
    {
        // 準備
        String[] jvnFileNames = {"nofile1.jvn"};

        // 実行
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                             jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail1 = {"nofile1.jvn"};
        String[] detail2 = {""};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 1, detail1,
                                  body[0]);
        AssertUtil.assertTelegram("jvnFile", "jvnFileContent", BYTE_ITEMMODE_KIND_STRING, 1,
                                  detail2, body[1]);
    }

    /**
     * [項番] 3-2-1 createJvnLogListTelegramのテスト。 <br />
     * ・引数を{""}にして、createJvnLogListTelegramを呼び出す。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogListTelegram_Empty()
    {
        // 準備
        String[] jvnFileNames = {};

        // 実行
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail = {};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 0, detail,
                                  body[0]);
    }

    /**
     * [項番] 3-2-2 createJvnLogListTelegramのテスト。 <br />
     * ・引数を{"file1.jvn"}にして、createJvnLogListTelegramを呼び出す。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogListTelegram_FileNum1()
    {
        // 準備
        String[] jvnFileNames = {"file1.jvn"};

        // 実行
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail = {"file1.jvn"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 1, detail,
                                  body[0]);
    }

    /**
     * [項番] 3-2-3 createJvnLogListTelegramのテスト。 <br />
     * ・引数を{"file1.jvn","file2.jvn","file3.jvn"}にして、createJvnLogListTelegramを呼び出す。
     * →作成した電文が指定したものになっている。<br />
     */
    public void testCreateJvnLogListTelegram_FileNum3()
    {
        // 準備
        String[] jvnFileNames = {"file1.jvn", "file2.jvn", "file3.jvn"};

        // 実行
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        Header header = telegram.getObjHeader();
        Body[] body = telegram.getObjBody();

        // ヘッダの検証
        assertEquals(BYTE_TELEGRAM_KIND_JVN_FILE_LIST, header.getByteTelegramKind());
        assertEquals(BYTE_REQUEST_KIND_RESPONSE, header.getByteRequestKind());

        String[] detail = {"file1.jvn", "file2.jvn", "file3.jvn"};

        // Bodyの検証
        AssertUtil.assertTelegram("jvnFile", "jvnFileName", BYTE_ITEMMODE_KIND_STRING, 3, detail,
                                  body[0]);
    }
}
