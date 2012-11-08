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
package jp.co.acroquest.endosnipe.communicator;

import static jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
import static jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants.BYTE_REQUEST_KIND_REQUEST;
import static jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.dgic.testing.framework.DJUnitTestCase;
import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.logger.SystemLogger;
import jp.co.acroquest.endosnipe.common.util.ResourceUtil;
import jp.co.acroquest.endosnipe.communicator.TelegramCreator.UpdateInvocationParam;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.util.TelegramAssertionUtil;

/**
 * TelegramCreatorの試験クラスです。
 * 
 * @author eriguchi
 */
public class TelegramCreatorTest extends DJUnitTestCase
{

    private static final String JVN_FILE_CONTENTS_SINGLE = "fileContents";

    private static final String JVN_FILE_NAMES_SINGLE = "TelegramCreatorTest_Single.jvn";

    private static final Object[] JVN_FILE_CONTENTS_MULTI =
            new Object[]{"fileContents0", "fileContents1", "fileContents2", "fileContents3",
                    "fileContents4"};

    private static final Object[] JVN_FILE_CONTENTS_ONE_CONTENT_NOT_NULL =
            new Object[]{"memContents0", null, "memContents2", "memContents3", "memContents4"};

    private static final String[] JVN_FILE_NAMES_MULTI =
            new String[]{"TelegramCreatorTest_Multi0.jvn", "TelegramCreatorTest_Multi1.jvn",
                    "TelegramCreatorTest_Multi2.jvn", "TelegramCreatorTest_Multi3.jvn",
                    "TelegramCreatorTest_Multi4.jvn"};
    
    private static final String[] JVN_FILE_CONTENTS_ONE_IS_LACKED =
        new String[]{"TelegramCreatorTest_Multi0.jvn", "TelegramCreatorTest_Multi1.jvn",
                "TelegramCreatorTest_Multi2.jvn", "TelegramCreatorTest_Multi3.jvn"};

    protected void setUp()
        throws Exception
    {
        super.setUp();
        File file =
                ResourceUtil.getResourceAsFile(getClass(), getClass().getSimpleName() + ".class");

        JavelinConfig config = new JavelinConfig();
        config.setJavelinFileDir(file.getParentFile().getAbsolutePath());

        SystemLogger.initSystemLog(config);
    }

    /**
     * @test JVNログ電文-要求電文:ログ単数(項目:3-1-1)
     * @condition requestKindが1、jvnFileNamesが単数("test1.jvn")
     * @result JVNログダウンロード要求電文が取得できること。
     *         設定されているログファイル名が単数であること。
     */
    public void testCreateJvnLogTelegram_Unit()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_REQUEST;
        List<String> jvnFileNames = new ArrayList<String>();
        jvnFileNames.add("test1.jvn");

        // 期待値
        Object[] expectedFileContents = new Object[]{JVN_FILE_CONTENTS_SINGLE};
        Object[] expectedJvnFileNames = jvnFileNames.toArray();

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogTelegram(requestKind, jvnFileNames);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test 要JVNログ電文-求電文:ログ複数(項目:3-1-2)
     * @condition requestKindが1
     *            jvnFileNamesが複数("test1.jvn","test2.jvn","test3.jvn","test4.jvn","test5.jvn")
     * @result JVNログダウンロード要求電文が取得できること。
     *         設定されているログファイル名が単数であること。
     */
    public void testCreateJvnLogTelegram_Multi()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_REQUEST;
        List<String> jvnFileNames = new ArrayList<String>();
        jvnFileNames.add("test1.jvn");
        jvnFileNames.add("test2.jvn");
        jvnFileNames.add("test3.jvn");
        jvnFileNames.add("test4.jvn");
        jvnFileNames.add("test5.jvn");

        // 期待値
        Object[] expectedFileContents = new Object[]{JVN_FILE_CONTENTS_SINGLE};
        Object[] expectedJvnFileNames = jvnFileNames.toArray();

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogTelegram(requestKind, jvnFileNames);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数／実体パラメータ指定null(項目:3-1-3)
     * @condition requestKindが2、jvnFileNamesが単数("test1.jvn")
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が単数であること。
     *         設定されているログファイル内容がローカルに配置していたJavelinログファイルの内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Unit()
    {
        // 準備
        String name = "TelegramCreatorTest_Unit.jvn";
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = new Object[]{name};
        Object[] jvnFileContents = null;

        Object[] expectedFileContents = new Object[]{JVN_FILE_CONTENTS_SINGLE};
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数(項目:3-1-4)
     * @condition requestKindが2、jvnFileNamesが複数(5件)
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容がローカルに配置していたJavelinログファイルの内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Multi_ContentNull()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = JVN_FILE_NAMES_MULTI;
        Object[] jvnFileContents = null;

        // 期待値
        Object[] expectedFileContents = JVN_FILE_CONTENTS_MULTI;
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数(項目:3-1-5)
     * @condition requestKindが2、jvnFileNamesが単数(1件), jvnFileContentsも単数(1件)
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が単数であること。
     *         設定されているログファイル内容がの内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Single_AllContentNotNull()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = new Object[]{JVN_FILE_NAMES_SINGLE};
        Object[] jvnFileContents = new Object[]{JVN_FILE_CONTENTS_SINGLE};

        // 期待値
        Object[] expectedFileContents = jvnFileContents;
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数(項目:3-1-6)
     * @condition requestKindが2、jvnFileNamesが複数(5件), jvnFileContentsも複数(5件)
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容がの内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Multi_AllContentNotNull()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = JVN_FILE_NAMES_MULTI;
        Object[] jvnFileContents = JVN_FILE_CONTENTS_MULTI;

        // 期待値
        Object[] expectedFileContents = jvnFileContents;
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数(項目:3-1-7)
     * @condition requestKindが2、jvnFileNamesが複数(5件), jvnFileContentsも複数(5件)
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容の内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Multi_OneContentNotNull()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = JVN_FILE_NAMES_MULTI;
        Object[] jvnFileContents = JVN_FILE_CONTENTS_ONE_CONTENT_NOT_NULL;

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;
        Object[] expectedFileContents =
                new Object[]{"memContents0", "fileContents1", "memContents2", "memContents3",
                        "memContents4"};

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ単数(項目:3-1-8)
     * @condition requestKindが2、jvnFileNamesが単数かつ存在しないファイル, jvnFileContentsがnull
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が単数であること。
     *         設定されているログファイル内容が""であること。
     */
    public void testCreateJvnLogDownloadTelegram_Single_ContentNull_FileNotFound()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames = new Object[]{"TelegramCreatorTest_Single_NotFound.jvn"};
        Object[] jvnFileContents = null;

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;
        Object[] expectedFileContents = new Object[]{""};

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ複数(項目:3-1-9)
     * @condition requestKindが2、jvnFileNamesが複数(5件)かつ存在しないファイル, jvnFileContentsがnull
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容が""であること。
     */
    public void testCreateJvnLogDownloadTelegram_Multi_ContentNull_FileNotFound()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_RESPONSE;
        Object[] jvnFileNames =
                new Object[]{"TelegramCreatorTest_Multi_NotFound0.jvn",
                        "TelegramCreatorTest_Multi_NotFound1.jvn",
                        "TelegramCreatorTest_Multi_NotFound2.jvn",
                        "TelegramCreatorTest_Multi_NotFound3.jvn",
                        "TelegramCreatorTest_Multi_NotFound4.jvn"};
        Object[] jvnFileContents = null;

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;
        Object[] expectedFileContents = new Object[]{"", "", "", "", ""};

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-通知電文:ログ単数／実体パラメータ指定(項目:3-1-10)
     * @condition requestKindが0、jvnFileNamesが単数, jvnFileContentsがnullでないこと。
     * @result JVNログダウンロード通知電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容の内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Notify_Single()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_NOTIFY;
        Object[] jvnFileNames = new Object[]{JVN_FILE_NAMES_SINGLE};
        Object[] jvnFileContents = {JVN_FILE_CONTENTS_SINGLE};

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;
        Object[] expectedFileContents = new Object[]{JVN_FILE_CONTENTS_SINGLE};

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_NOTIFY,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ電文-応答電文:ログ複数／実体パラメータ指定null(項目:3-1-11)
     * @condition requestKindが2、jvnFileNamesが複数(5件), jvnFileContentsがnull
     * @result JVNログダウンロード応答電文が取得できること。
     *         設定されているログファイル名が複数であること。
     *         設定されているログファイル内容の内容と等しいこと。
     */
    public void testCreateJvnLogDownloadTelegram_Notify_Multi()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_NOTIFY;
        Object[] jvnFileNames = JVN_FILE_NAMES_MULTI;
        Object[] jvnFileContents = null;

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;
        Object[] expectedFileContents = JVN_FILE_CONTENTS_MULTI;

        // 実施
        Telegram telegram =
                TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);

        // 検証
        TelegramAssertionUtil.assertJvnLogDownloadTelegram(BYTE_REQUEST_KIND_NOTIFY,
                                                           expectedFileContents,
                                                           expectedJvnFileNames, telegram);
    }
    
    /**
     * @test JVNログ電文-応答電文:ログと実体パラメータが異なる
     * @condition jvnFileNamesが5件, jvnFileContentsが4件
     * @result IllegalArgumentExceptionがスローされること。
     */
    public void testCreateJvnLogDownloadTelegram_IllegalArgument()
    {
        // 準備
        byte requestKind = BYTE_REQUEST_KIND_NOTIFY;
        Object[] jvnFileNames = JVN_FILE_NAMES_MULTI;
        Object[] jvnFileContents = JVN_FILE_CONTENTS_ONE_IS_LACKED;

        // 実施・検証
        try
        {
            TelegramCreator.createJvnLogDownloadTelegram(requestKind, jvnFileNames,
                                                             jvnFileContents, 0);
            assertTrue(false);
        }
        catch (IllegalArgumentException ex)
        {
            assertTrue(true);
        }
    }

    /**
     * @test JVNログ一覧電文-JVNログnull(項目:5-1-1)
     * @condition jvnFileNamesがnull
     * @result 電文が取得できず、nullが返ること。
     */
    public void testCreateJvnLogListTelegram_Null()
    {
        // 準備
        String[] jvnFileNames = null;

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        assertNull(telegram);
    }

    /**
     * @test JVNログ一覧電文-JVNログ空リスト(項目:5-1-2)
     * @condition jvnFileNamesが空リスト
     * @result 電文のBody部のjvnFileの項目値が0件であること。
     */
    public void testCreateJvnLogListTelegram_Empty()
    {
        // 準備
        String[] jvnFileNames = new String[]{};

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        TelegramAssertionUtil.assertJvnLogListTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                       expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ一覧電文-JVNログ単数(項目:5-1-3)
     * @condition jvnFileNamesが単数
     * @result 電文のBody部のjvnFileの項目値が指定した内容と同一であること。
     */
    public void testCreateJvnLogListTelegram_Single()
    {
        // 準備
        String[] jvnFileNames = new String[]{JVN_FILE_NAMES_SINGLE};

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        TelegramAssertionUtil.assertJvnLogListTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                       expectedJvnFileNames, telegram);
    }

    /**
     * @test JVNログ一覧電文-JVNログ複数(項目:5-1-4)
     * @condition jvnFileNamesが複数
     * @result 電文のBody部のjvnFileの項目値が指定した内容と同一であること。
     */
    public void testCreateJvnLogListTelegram_Multi()
    {
        // 準備
        String[] jvnFileNames = JVN_FILE_NAMES_MULTI;

        // 期待値
        Object[] expectedJvnFileNames = jvnFileNames;

        // 実施
        Telegram telegram = TelegramCreator.createJvnLogListTelegram(jvnFileNames);

        // 検証
        TelegramAssertionUtil.assertJvnLogListTelegram(BYTE_REQUEST_KIND_RESPONSE,
                                                       expectedJvnFileNames, telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-対象Invocation null(項目:6-1-1)
     * @condition Invocationがnull
     * @result 電文のBodyが空であること。
     */
    public void testCreateUpdateInvocationTelegramTelegram_Null()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray = null;

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-対象Invocation空リスト(項目:6-1-2)
     * @condition 対象Invocationが空リスト
     * @result 電文のBodyが空であること。
     */
    public void testCreateUpdateInvocationTelegramTelegram_Empty()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray = new UpdateInvocationParam[]{};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求対象Invocation単数(項目:6-1-3)
     * @condition 対象Invocation単数
     * @result 各invocationの4つの項目が、パラメータの通りに電文に設定されていること。
     */
    public void testCreateUpdateInvocationTelegramTelegram_Single()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{new UpdateInvocationParam("className", "methodName",
                                                                      Boolean.TRUE, Boolean.TRUE,
                                                                      Long.valueOf(5000),
                                                                      Long.valueOf(3000))};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求対象Invocation複数(項目:6-1-4)
     * @condition 対象Invocation複数
     * @result 各invocationの4つの項目が、パラメータの通りに電文に設定されていること。
     */
    public void testCreateUpdateInvocationTelegramTelegram_Multi()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{
                        new UpdateInvocationParam("className", "methodName", Boolean.TRUE,
                                                  Boolean.TRUE, Long.valueOf(5000),
                                                  Long.valueOf(3000)),
                        new UpdateInvocationParam("className1", "methodName1", Boolean.TRUE,
                                                  Boolean.FALSE, Long.valueOf(4000),
                                                  Long.valueOf(2000)),
                        new UpdateInvocationParam("className2", "methodName2", Boolean.TRUE,
                                                  Boolean.FALSE, Long.valueOf(3000),
                                                  Long.valueOf(1000)),
                        new UpdateInvocationParam("className3", "methodName3", Boolean.TRUE,
                                                  Boolean.FALSE, Long.valueOf(2000),
                                                  Long.valueOf(1000)),
                        new UpdateInvocationParam("className4", "methodName4", Boolean.TRUE,
                                                  Boolean.FALSE, Long.valueOf(1000),
                                                  Long.valueOf(2000))

                };

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-transactionGraph null(項目:6-1-5)
     * @condition transactionGraph null
     * @result 電文のBody部に項目targetが存在しないこと。
     */
    public void testCreateUpdateInvocationTelegramTelegram_TransactionNull()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{new UpdateInvocationParam("className", "methodName",
                                                                      null, Boolean.TRUE,
                                                                      Long.valueOf(5000),
                                                                      Long.valueOf(3000))};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-target null(項目:6-1-6)
     * @condition target null
     * @result 電文のBody部に項目alarmThresholdが存在しないこと。
     */
    public void testCreateUpdateInvocationTelegramTelegram_TargetNull()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{new UpdateInvocationParam("className", "methodName",
                                                                      Boolean.TRUE, null,
                                                                      Long.valueOf(5000),
                                                                      Long.valueOf(3000))};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-alarmThreshold null(項目:6-1-7)
     * @condition alarmThreshold null
     * @result 電文のBody部に項目alarmThresholdが存在しないこと。
     */
    public void testCreateUpdateInvocationTelegramTelegram_AlarmThresholdNull()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{new UpdateInvocationParam("className", "methodName",
                                                                      Boolean.TRUE, Boolean.TRUE,
                                                                      null, Long.valueOf(3000))};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test 計測対象・トランザクショングラフ出力更新要求-alarmCpuThreshold null(項目:6-1-8)
     * @condition alarmCpuThreshold null
     * @result 電文のBody部に項目alarmCpuThresholdが存在しないこと。
     */
    public void testCreateUpdateInvocationTelegramTelegram_AlarmCpuThresholdNull()
    {
        // 準備
        UpdateInvocationParam[] invocationParamArray =
                new UpdateInvocationParam[]{new UpdateInvocationParam("className", "methodName",
                                                                      Boolean.TRUE, Boolean.TRUE,
                                                                      Long.valueOf(3000), null)};

        // 期待値
        UpdateInvocationParam[] expectedInvocationParamArray = invocationParamArray;

        // 実施
        Telegram telegram = TelegramCreator.createUpdateInvocationTelegram(invocationParamArray);

        // 検証
        TelegramAssertionUtil.assertUpdateInvocationTelegramTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                                     expectedInvocationParamArray,
                                                                     telegram);
    }

    /**
     * @test クラス削除電文-対象クラスnull(項目:7-1-1)
     * @condition 対象クラス null
     * @result 電文のBody部が空であること。
     */
    public void testCreateRemoveClassTelegram_Null()
    {
        // 準備
        List<String> classNameList = null;

        // 期待値
        List<String> expectedClassNameList = classNameList;

        // 実施
        Telegram telegram = TelegramCreator.createRemoveClassTelegram(classNameList);

        // 検証
        TelegramAssertionUtil.assertRemoveClassTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                        expectedClassNameList, telegram);
    }

    /**
     * @test クラス削除電文-対象クラス空リスト(項目:7-1-2)
     * @condition 対象クラス 空リスト
     * @result 電文のBody部が空であること。
     */
    public void testCreateRemoveClassTelegram_Empty()
    {
        // 準備
        List<String> classNameList = new ArrayList<String>();

        // 期待値
        List<String> expectedClassNameList = classNameList;

        // 実施
        Telegram telegram = TelegramCreator.createRemoveClassTelegram(classNameList);

        // 検証
        TelegramAssertionUtil.assertRemoveClassTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                        expectedClassNameList, telegram);
    }

    /**
     * @test クラス削除電文-対象クラス単数(項目:7-1-3)
     * @condition 対象クラス単数
     * @result 電文のBody部に、パラメータで指定した全てのクラス名が設定されていること。
     */
    public void testCreateRemoveClassTelegram_Single()
    {
        // 準備
        List<String> classNameList = new ArrayList<String>();
        classNameList.add("className1");

        // 期待値
        List<String> expectedClassNameList = classNameList;

        // 実施
        Telegram telegram = TelegramCreator.createRemoveClassTelegram(classNameList);

        // 検証
        TelegramAssertionUtil.assertRemoveClassTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                        expectedClassNameList, telegram);
    }

    /**
     * @test クラス削除電文-対象クラス複数(項目:7-1-4)
     * @condition 対象クラス複数
     * @result 電文のBody部に、パラメータで指定した全てのクラス名が設定されていること。
     */
    public void testCreateRemoveClassTelegram_Multi()
    {
        // 準備
        List<String> classNameList = new ArrayList<String>();
        classNameList.add("className1");
        classNameList.add("className2");
        classNameList.add("className3");
        classNameList.add("className4");
        classNameList.add("className5");

        // 期待値
        List<String> expectedClassNameList = classNameList;

        // 実施
        Telegram telegram = TelegramCreator.createRemoveClassTelegram(classNameList);

        // 検証
        TelegramAssertionUtil.assertRemoveClassTelegram(BYTE_REQUEST_KIND_REQUEST,
                                                        expectedClassNameList, telegram);
    }
}
