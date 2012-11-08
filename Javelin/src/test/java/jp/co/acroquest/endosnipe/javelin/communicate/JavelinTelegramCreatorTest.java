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
package jp.co.acroquest.endosnipe.javelin.communicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.communicator.util.TelegramAssertionUtil;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import junit.framework.TestCase;

/**
 * JavelinTelegramCreatorのテストクラスです。
 * 
 * @author eriguchi
 */
public class JavelinTelegramCreatorTest extends TestCase
{
    /**
     * @test 状態通知電文変換-通知対象単数 累積時間無し(項目:1-1-1)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_NoTime()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        List<Long> accumulatedTimes = null;
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = new ArrayList<Long>();
        expectedAccumulatedTimes.add(Long.valueOf(0));
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-通知対象単数 累積時間有り(項目:1-1-2)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_Time()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-通知対象複数 累積時間無し(項目:1-1-3)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Multi_NoTime()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        invocations.add(new Invocation("processName", "className2", "methodName2", 4000));
        List<Long> accumulatedTimes = null;
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = new ArrayList<Long>();
        expectedAccumulatedTimes.add(Long.valueOf(0));
        expectedAccumulatedTimes.add(Long.valueOf(0));
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-通知対象複数 累積時間有り(項目:1-1-4)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Multi_Time()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        invocations.add(new Invocation("processName", "className2", "methodName2", 4000));
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-通知対象複数 累積時間不足(項目:1-1-5)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Multi_LackTime()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        invocations.add(new Invocation("processName", "className2", "methodName2", 4000));
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = new ArrayList<Long>();
        expectedAccumulatedTimes.add(Long.valueOf(1));
        expectedAccumulatedTimes.add(Long.valueOf(0));
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-計測対象メソッド(項目:1-1-6)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_Target()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        String className = "className";
        String methodName = "methodName";
        Invocation invocation = new Invocation("processName", className, methodName, 3000);
        invocations.add(invocation);
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
        ExcludeMonitor.addTarget(invocation);

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-計測対象外メソッド(項目:1-1-7)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_NotTarget()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        String className = "className";
        String methodName = "methodName";
        Invocation invocation = new Invocation("processName", className, methodName, 3000);
        invocations.add(invocation);
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
        ExcludeMonitor.addExclude(invocation);

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("false");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知電文変換-呼び出し元メソッド　単数(項目:1-1-8)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_CallerSingle()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        String className = "className";
        String methodName = "methodName";
        Invocation invocation =
                new Invocation("processName", className, methodName, 3000);
        invocation.addCaller(new Invocation("processName", "callerClass", "callerMethod", 3000));
        invocations.add(invocation);
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
        ExcludeMonitor.addTarget(invocation);
        ExcludeMonitor.removeExclude(invocation);

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        List<String> expectedCallerNames = Arrays.asList(new String[]{"callerClass"});

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget, expectedCallerNames,
                            telegram);
    }

    /**
     * @test 状態通知電文変換-呼び出し元メソッド　複数(項目:1-1-9)
     * @condition 
     * @result
     */
    public void testCreate_Alert_Single_CallerMulti()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        String className = "className";
        String methodName = "methodName";
        Invocation invocation =
                new Invocation("processName", className, methodName, 3000);
        invocation.addCaller(new Invocation("processName", "callerClass", "callerMethod", 3000));
        invocation.addCaller(new Invocation("processName", "callerClass2", "callerMethod2", 3000));
        invocations.add(invocation);
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_ALERT;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_NOTIFY;
        ExcludeMonitor.addTarget(invocation);
        ExcludeMonitor.removeExclude(invocation);

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        List<String> expectedCallerNames = Arrays.asList(new String[]{"callerClass2", "callerClass"});

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget, expectedCallerNames,
                            telegram);
    }

    /**
     * @test 状態通知応答電文変換-通知対象単数 累積時間無し(項目:1-2-1)
     * @condition 
     * @result
     */
    public void testCreate_Get_Single_NoTime()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        List<Long> accumulatedTimes = null;
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_GET;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = new ArrayList<Long>();
        expectedAccumulatedTimes.add(Long.valueOf(0));
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知応答電文変換-通知対象単数 累積時間有り(項目:1-2-2)
     * @condition 
     * @result
     */
    public void testCreate_Get_Single_Time()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_GET;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知応答電文変換-通知対象複数 累積時間無し(項目:1-2-3)
     * @condition 
     * @result
     */
    public void testCreate_Get_Multi_NoTime()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        invocations.add(new Invocation("processName", "className2", "methodName2", 4000));
        List<Long> accumulatedTimes = null;
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_GET;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = new ArrayList<Long>();
        expectedAccumulatedTimes.add(Long.valueOf(0));
        expectedAccumulatedTimes.add(Long.valueOf(0));
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    /**
     * @test 状態通知応答電文変換-通知対象複数 累積時間有り(項目:1-2-4)
     * @condition 
     * @result
     */
    public void testCreate_Get_Multi_Time()
    {
        // 準備
        List<Invocation> invocations = new ArrayList<Invocation>();
        invocations.add(new Invocation("processName", "className", "methodName", 3000));
        invocations.add(new Invocation("processName", "className2", "methodName2", 4000));
        List<Long> accumulatedTimes = new ArrayList<Long>();
        accumulatedTimes.add(Long.valueOf(1));
        accumulatedTimes.add(Long.valueOf(1));
        byte telegramKind = TelegramConstants.BYTE_TELEGRAM_KIND_GET;
        byte requestKind = TelegramConstants.BYTE_REQUEST_KIND_RESPONSE;

        // 期待値
        List<Invocation> expectedInvocations = invocations;
        List<Long> expectedAccumulatedTimes = accumulatedTimes;
        byte expectedTelegramKind = telegramKind;
        byte expectedRequestKind = requestKind;
        List<String> expectedIsTarget = new ArrayList<String>();
        expectedIsTarget.add("true");
        expectedIsTarget.add("true");

        // 実施
        Telegram telegram =
                JavelinTelegramCreator.create(invocations, accumulatedTimes, telegramKind,
                                              requestKind);

        // 検証
        assertAlertTelegram(expectedTelegramKind, expectedRequestKind, expectedInvocations,
                            expectedAccumulatedTimes, expectedIsTarget,
                            Arrays.asList(new String[]{}), telegram);
    }

    
    private void assertAlertTelegram(byte expectedTelegramKind, byte expectedRequestKind,
            List<Invocation> expectedInvocations, List<Long> expectedAccumulatedTimes,
            List<String> expectedIsTarget, List<String> expectedCallerNames, Telegram telegram)
    {
        // ヘッダの検証
        TelegramAssertionUtil.assertHeader(expectedTelegramKind, expectedRequestKind,
                                           telegram.getObjHeader());

        // Invocationの検証
        int bodyIndex = 0;
        for (int index = 0; index < expectedInvocations.size(); index++)
        {
            Invocation invocation = expectedInvocations.get(index);
            String className = invocation.getClassName();
            String methodName = invocation.getMethodName();

            String classMethodName = className + "###CLASSMETHOD_SEPARATOR###" + methodName;
            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_CALL_COUNT,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getCount()},
                                                 telegram.getObjBody()[bodyIndex++]);

            classMethodName = "";

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_CURRENT_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{expectedAccumulatedTimes.get(index)},
                                                 telegram.getObjBody()[bodyIndex++]);
//
            

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_TOTAL_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedTotal()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MAXIMUM_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MINIMUM_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_TOTAL_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedCpuTotal()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MAXIMUM_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedCpuMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MINIMUM_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedCpuMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_TOTAL_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedUserTotal()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MAXIMUM_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedUserMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ACCUMULATED_MINIMUM_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAccumulatedUserMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);
            
            //
            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_TOTAL_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getTotal()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MAXIMUM_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MINIMUM_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_TOTAL_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{Long.valueOf(0)},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MAXIMUM_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getCpuMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MINIMUM_CPU_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getCpuMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_TOTAL_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getUserTotal()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MAXIMUM_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getUserMaximum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_MINIMUM_USER_INTERVAL,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getUserMinimum()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getThrowableCount()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ALL_CALLER_NAMES,
                                                 ItemType.ITEMTYPE_STRING,
                                                 expectedCallerNames.size(),
                                                 expectedCallerNames.toArray(),
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_TARGET,
                                                 ItemType.ITEMTYPE_STRING,
                                                 1,
                                                 new String[]{expectedIsTarget.get(index)},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_TRANSACTION_GRAPH,
                                                 ItemType.ITEMTYPE_STRING,
                                                 1,
                                                 new String[]{"false"},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ALARM_THRESHOLD,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAlarmThreshold()},
                                                 telegram.getObjBody()[bodyIndex++]);

            TelegramAssertionUtil.assertTelegram(classMethodName,
                                                 TelegramConstants.ITEMNAME_ALARM_CPU_THRESHOLD,
                                                 ItemType.ITEMTYPE_LONG,
                                                 1,
                                                 new Object[]{invocation.getAlarmCpuThreshold()},
                                                 telegram.getObjBody()[bodyIndex++]);
        }
    }
}
