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
package jp.co.acroquest.endosnipe.communicator.util;

import java.util.List;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.communicator.TelegramCreator.UpdateInvocationParam;
import jp.co.acroquest.endosnipe.communicator.entity.Body;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import junit.framework.TestCase;

/**
 * 検証用ユーティリティ
 * @author fujii
 *
 */
public class TelegramAssertionUtil extends TestCase
{
    private TelegramAssertionUtil()
    {

    }

    /**
     * 電文のBodyの検証を行う。(電文の中身も検証する)
     * @param objName オブジェクト名
     * @param itemName 項目名
     * @param itemMode モード
     * @param loop ループ回数
     * @param detail 詳細
     * @param body 受け取った電文
     */
    public static void assertTelegram(final String objName, final String itemName,
            final ItemType itemMode, final int loop, final Object[] detail, final Body body)
    {
        assertEquals(objName, body.getStrObjName());
        assertEquals(itemName, body.getStrItemName());
        assertEquals(itemMode, body.getByteItemMode());
        assertEquals(loop, body.getIntLoopCount());
        assertDetail(detail, body);
    }

    /**
     * 電文のBodyの検証を行う。(電文の中身は検証しない)
     * @param objName オブジェクト名
     * @param itemName 項目名
     * @param itemMode モード
     * @param loop ループ回数
     * @param body 受け取った電文
     */
    public static void assertResourceTelegram(final String objName, final String itemName,
            final byte itemMode, final int loop, final Body body)
    {
        assertEquals(objName, body.getStrObjName());
        assertEquals(itemName, body.getStrItemName());
        assertEquals(itemMode, body.getByteItemMode());
        assertEquals(loop, body.getIntLoopCount());
    }

    /**
     * Bodyの詳細を検証する。
     * @param detail 期待する詳細
     * @param body 受け取った電文
     */
    public static void assertDetail(final Object[] detail, final Body body)
    {
        if (detail.length != body.getObjItemValueArr().length)
        {
            fail();
            return;
        }
        Object[] objValue = body.getObjItemValueArr();

        for (int num = 0; num < detail.length; num++)
        {
            assertEquals(detail[num], objValue[num]);
        }
    }

    /**
     * ヘッダの検証を行ないます。
     * @param telegramKind 電文種別
     * @param requestKind 電文応答種別
     * @param header ヘッダ
     */
    public static void assertHeader(final byte telegramKind, final byte requestKind,
            final Header header)
    {
        assertEquals(telegramKind, header.getByteTelegramKind());
        assertEquals(requestKind, header.getByteRequestKind());
    }

    /**
     * JVNログダウンロード応答電文の検証を行います。
     * 
     * @param expectedRequestKind リクエスト種別。
     * @param expectedFileContents ファイルの内容。
     * @param expectedJvnFileNames ファイル名。
     */
    public static void assertJvnLogDownloadTelegram(byte expectedRequestKind,
            Object[] expectedFileContents, Object[] expectedJvnFileNames, Telegram telegram)
    {
        // 検証
        assertNotNull(telegram);

        int expectedObjBodyLength = 1;
        if (expectedRequestKind != TelegramConstants.BYTE_REQUEST_KIND_REQUEST)
        {
            expectedObjBodyLength = 4;
        }

        // 電文本体サイズの検証
        assertEquals(expectedObjBodyLength, telegram.getObjBody().length);

        // ヘッダの検証
        assertHeader(TelegramConstants.BYTE_TELEGRAM_KIND_JVN_FILE, expectedRequestKind,
                     telegram.getObjHeader());

        // jvnファイル名の検証
        assertTelegram(TelegramConstants.OBJECTNAME_JVN_FILE,
                       TelegramConstants.ITEMNAME_JVN_FILE_NAME,
                       ItemType.ITEMTYPE_STRING, expectedJvnFileNames.length,
                       expectedJvnFileNames, telegram.getObjBody()[0]);

        // jvnファイル内容の検証
        if (expectedRequestKind != TelegramConstants.BYTE_REQUEST_KIND_REQUEST)
        {
            JavelinConfig config = new JavelinConfig();
            assertTelegram(TelegramConstants.OBJECTNAME_JVN_FILE,
                           TelegramConstants.ITEMNAME_JVN_FILE_CONTENT,
                           ItemType.ITEMTYPE_STRING,
                           expectedFileContents.length, expectedFileContents,
                           telegram.getObjBody()[1]);
            assertTelegram(TelegramConstants.OBJECTNAME_JVN_FILE,
                           TelegramConstants.ITEMNAME_ALARM_THRESHOLD,
                           ItemType.ITEMTYPE_LONG,
                           1, new Object[]{config.getAlarmThreshold()},
                           telegram.getObjBody()[2]);
            assertTelegram(TelegramConstants.OBJECTNAME_JVN_FILE,
                           TelegramConstants.ITEMNAME_ALARM_CPU_THRESHOLD,
                           ItemType.ITEMTYPE_LONG,
                           1, new Object[]{config.getAlarmCpuThreashold()},
                           telegram.getObjBody()[3]);
        }

    }

    /**
     * JVNログ一覧取得応答電文の検証を行います。
     * 
     * @param expectedRequestKind リクエスト種別。
     * @param expectedJvnFileNames ファイル名。
     */
    public static void assertJvnLogListTelegram(byte expectedRequestKind,
            Object[] expectedJvnFileNames, Telegram telegram)
    {
        // 検証
        assertNotNull(telegram);

        int expectedObjBodyLength = 1;

        // ヘッダの検証
        assertHeader(TelegramConstants.BYTE_TELEGRAM_KIND_JVN_FILE_LIST, expectedRequestKind,
                     telegram.getObjHeader());

        // 電文本体サイズの検証
        assertEquals(expectedObjBodyLength, telegram.getObjBody().length);

        // jvnファイル名の検証
        assertTelegram(TelegramConstants.OBJECTNAME_JVN_FILE,
                       TelegramConstants.ITEMNAME_JVN_FILE_NAME,
                       ItemType.ITEMTYPE_STRING, expectedJvnFileNames.length,
                       expectedJvnFileNames, telegram.getObjBody()[0]);
    }

    /**
     * 計測対象・トランザクショングラフ出力更新要求電文の検証を行います。
     * 
     * @param expectedRequestKind リクエスト種別。
     * @param expectedParam Invocation。
     */
    public static void assertUpdateInvocationTelegramTelegram(byte expectedRequestKind,
            UpdateInvocationParam[] expectedParam, Telegram telegram)
    {
        // 検証
        assertNotNull(telegram);

        // ヘッダの検証
        assertHeader(TelegramConstants.BYTE_TELEGRAM_KIND_UPDATE_TARGET, expectedRequestKind,
                     telegram.getObjHeader());

        // Invocationの検証
        int currentIndex = 0;
        if (expectedParam != null)
        {
            for (int index = 0; index < expectedParam.length; index++)
            {
                UpdateInvocationParam param = expectedParam[index];
                String className = param.getClassName();
                String methodName = param.getMethodName();
                String classMethodName = className + "###CLASSMETHOD_SEPARATOR###" + methodName;
    
                if (param.isTransactionGraphOutput() != null)
                {
                    assertTelegram(classMethodName, TelegramConstants.ITEMNAME_TRANSACTION_GRAPH,
                                   ItemType.ITEMTYPE_STRING, 1,
                                   new Object[]{String.valueOf(param.isTransactionGraphOutput())},
                                   telegram.getObjBody()[currentIndex++]);
                }
    
                if (param.isTarget() != null)
                {
                    assertTelegram(classMethodName, TelegramConstants.ITEMNAME_TARGET,
                                   ItemType.ITEMTYPE_STRING, 1,
                                   new Object[]{String.valueOf(param.isTarget())},
                                   telegram.getObjBody()[currentIndex++]);
                }
    
                if (param.getAlarmThreshold() != null)
                {
                    assertTelegram(classMethodName, TelegramConstants.ITEMNAME_ALARM_THRESHOLD,
                                   ItemType.ITEMTYPE_LONG, 1,
                                   new Object[]{Long.valueOf(param.getAlarmThreshold())},
                                   telegram.getObjBody()[currentIndex++]);
                }
    
                if (param.getAlarmCpuThreshold() != null)
                {
                    assertTelegram(classMethodName, TelegramConstants.ITEMNAME_ALARM_CPU_THRESHOLD,
                                   ItemType.ITEMTYPE_LONG, 1,
                                   new Object[]{Long.valueOf(param.getAlarmCpuThreshold())},
                                   telegram.getObjBody()[currentIndex++]);
                }
            }
        }

        // 電文本体サイズの検証
        assertEquals(currentIndex, telegram.getObjBody().length);
    }

    /**
     * 計測対象・トランザクショングラフ出力更新要求電文の検証を行います。
     * 
     * @param expectedRequestKind リクエスト種別。
     * @param expectedClassNameList クラス名のリスト。
     */
    public static void assertRemoveClassTelegram(byte expectedRequestKind,
            List<String> expectedClassNameList, Telegram telegram)
    {
        // 検証
        assertNotNull(telegram);

        // ヘッダの検証
        assertHeader(TelegramConstants.BYTE_TELEGRAM_KIND_REMOVE_CLASS, expectedRequestKind,
                     telegram.getObjHeader());

        // Invocationの検証
        if (expectedClassNameList != null)
        {
            for (int index = 0; index < expectedClassNameList.size(); index++)
            {
                String className = expectedClassNameList.get(index);
    
                assertTelegram(className, TelegramConstants.ITEMNAME_CLASSTOREMOVE,
                               ItemType.ITEMTYPE_STRING, 0, new Object[]{},
                               telegram.getObjBody()[index]);
            }
            // 電文本体サイズの検証
            assertEquals(expectedClassNameList.size(), telegram.getObjBody().length);
        }
        else
        {
            // 電文本体サイズの検証
            assertEquals(0, telegram.getObjBody().length);
        }
    }
}
