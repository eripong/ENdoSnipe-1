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

import java.util.Arrays;
import java.util.List;

import jp.co.acroquest.endosnipe.common.entity.ItemType;
import jp.co.acroquest.endosnipe.communicator.TelegramUtil;
import jp.co.acroquest.endosnipe.communicator.entity.Header;
import jp.co.acroquest.endosnipe.communicator.entity.ResponseBody;
import jp.co.acroquest.endosnipe.communicator.entity.Telegram;
import jp.co.acroquest.endosnipe.communicator.entity.TelegramConstants;
import jp.co.acroquest.endosnipe.javelin.MBeanManager;
import jp.co.acroquest.endosnipe.javelin.bean.Component;
import jp.co.acroquest.endosnipe.javelin.bean.ExcludeMonitor;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.util.ArrayList;

/**
 *  Invocationから電文を作成するクラスです。<br />
 * @author acroquest
 *
 */
public class JavelinTelegramCreator implements TelegramConstants
{
    private static final int TELEGRAM_ITEM_COUNT = 26;

    private JavelinTelegramCreator()
    {
        // Do Nothing.
    }

    /***
     * 全てのInvocationのリストを取得し、電文に変換します。<br />
     * 
     * @return 全Invocationのリストを電文に変換したもの。
     */
    public static List<byte[]> createAll()
    {
        // 電文データを取る
        Component[] objComponentArr = MBeanManager.getAllComponents();
        List<Invocation> invocationList = new ArrayList<Invocation>();

        // 電文数を統計する
        for (int i = 0; i < objComponentArr.length; i++)
        {
            invocationList.addAll(Arrays.asList(objComponentArr[i].getAllInvocation()));
        }

        Telegram objTelegram =
                create(invocationList, BYTE_TELEGRAM_KIND_GET, BYTE_REQUEST_KIND_RESPONSE);

        // 電文は、object ⇒ byte[] に変換する
        List<byte[]> byteList = TelegramUtil.createTelegram(objTelegram);

        // 返却する
        return byteList;
    }

    /**
     * 指定したinvocationのリストから、そのオブジェクトを表す電文を作成します。<br />
     * 
     * @param invocations inovocationのリスト
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @return invocationのリストから作成した電文
     */
    public static Telegram create(final List<Invocation> invocations, final byte telegramKind,
            final byte requestKind)
    {
        return create(invocations, null, telegramKind, requestKind);
    }

    /**
     * 指定したinvocationのリストから、そのオブジェクトを表す電文を作成します。<br />
     * 
     * @param invocations inovocationのリスト
     * @param accumulatedTimes inovocationに対応する、累積時間のリスト
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @return invocationのリストから作成した電文
     */
    public static Telegram create(final List<Invocation> invocations,
            final List<Long> accumulatedTimes, final byte telegramKind, final byte requestKind)
    {
        return create(invocations, accumulatedTimes, telegramKind, requestKind, 0);
    }

    /**
     * 指定したinvocationのリストから、そのオブジェクトを表す電文を作成します。<br />
     * 
     * @param invocations inovocationのリスト
     * @param accumulatedTimes inovocationに対応する、累積時間のリスト
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @param telegramId 電文 ID
     * @return invocationのリストから作成した電文
     */
    public static Telegram create(final List<Invocation> invocations,
            final List<Long> accumulatedTimes, final byte telegramKind, final byte requestKind,
            final long telegramId)
    {
        // 電文頭部を作る【とりあえず、電文長を設定しない】
        Header objHeader = new Header();
        objHeader.setId(telegramId);
        objHeader.setByteRequestKind(requestKind);
        objHeader.setByteTelegramKind(telegramKind);

        // 電文本体を作る
        ResponseBody[] bodies = new ResponseBody[invocations.size() * TELEGRAM_ITEM_COUNT];

        for (int index = 0; index < invocations.size(); index++)
        {
            Invocation invocation = invocations.get(index);

            // オブジェクト名を取得する
            StringBuffer strObjName = new StringBuffer();
            strObjName.append(invocation.getClassName());
            strObjName.append(CLASSMETHOD_SEPARATOR);
            strObjName.append(invocation.getMethodName());
            String objName = strObjName.toString();

            // 項目説明を置けるリスト
            Object[] objItemValueArr = null;
            int bodyIndex = index * TELEGRAM_ITEM_COUNT;

            // 呼び出し回数
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCount());
            bodies[bodyIndex + 0] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_CALL_COUNT,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // 2つ目以降はどのクラス、メソッドの情報か明らかのため、クラス名、メソッド名を空にする
            objName = "";

            // 処理時間
            objItemValueArr = new Long[1];
            if (accumulatedTimes != null && index < accumulatedTimes.size())
            {
                objItemValueArr[0] = Long.valueOf(accumulatedTimes.get(index));
            }
            else
            {
                objItemValueArr[0] = Long.valueOf(0);
            }
            bodies[bodyIndex + 1] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_CURRENT_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // 積算合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedTotal());
            bodies[bodyIndex + 2] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_TOTAL_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算最大時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedMaximum());
            bodies[bodyIndex + 3] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MAXIMUM_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算最小時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedMinimum());
            bodies[bodyIndex + 4] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MINIMUM_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            
            // 積算CPU合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedCpuTotal());
            bodies[bodyIndex + 5] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_TOTAL_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算CPU最大時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedCpuMaximum());
            bodies[bodyIndex + 6] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MAXIMUM_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算CPU最小時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedCpuMinimum());
            bodies[bodyIndex + 7] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MINIMUM_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            
            // 積算USER合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedUserTotal());
            bodies[bodyIndex + 8] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_TOTAL_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算USER最大時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedUserMaximum());
            bodies[bodyIndex + 9] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MAXIMUM_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            // 積算USER最小時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAccumulatedUserMinimum());
            bodies[bodyIndex + 10] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ACCUMULATED_MINIMUM_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            
            // 合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getTotal());
            bodies[bodyIndex + 11] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_TOTAL_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // 最大処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getMaximum());
            bodies[bodyIndex + 12] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MAXIMUM_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // 最小処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getMinimum());
            bodies[bodyIndex + 13] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MINIMUM_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // CPU合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuTotal());
            bodies[bodyIndex + 14] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_TOTAL_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // CPU最大処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuMaximum());
            bodies[bodyIndex + 15] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MAXIMUM_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // CPU最小処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuMinimum());
            bodies[bodyIndex + 16] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MINIMUM_CPU_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // User合計時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserTotal());
            bodies[bodyIndex + 17] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_TOTAL_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // User最大処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserMaximum());
            bodies[bodyIndex + 18] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MAXIMUM_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // User最小処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserMinimum());
            bodies[bodyIndex + 19] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_MINIMUM_USER_INTERVAL,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // 例外発生回数
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getThrowableCount());
            bodies[bodyIndex + 20] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_JAVAPROCESS_EXCEPTION_OCCURENCE_COUNT,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // メソッドの呼び出し元 クラス名
            Invocation[] callerInvocations = invocation.getAllCallerInvocation();
            String[] callerNames = new String[callerInvocations.length];
            for (int callerIndex = 0; callerIndex < callerInvocations.length; callerIndex++)
            {
                callerNames[callerIndex] = callerInvocations[callerIndex].getClassName();
            }
            bodies[bodyIndex + 21] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ALL_CALLER_NAMES,
                                                    ItemType.ITEMTYPE_STRING, callerNames);

            // 計測対象か否か
            objItemValueArr = new String[1];
            boolean isTarget = isTarget(invocation);
            objItemValueArr[0] = String.valueOf(isTarget);
            bodies[bodyIndex + 22] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_TARGET,
                                                    ItemType.ITEMTYPE_STRING, objItemValueArr);

            // トランザクショングラフ出力対象か否か
            objItemValueArr = new String[1];
            objItemValueArr[0] = String.valueOf(invocation.isResponseGraphOutputTarget());
            bodies[bodyIndex + 23] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_TRANSACTION_GRAPH,
                                                    ItemType.ITEMTYPE_STRING, objItemValueArr);

            // TATアラーム閾値
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAlarmThreshold());
            bodies[bodyIndex + 24] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ALARM_THRESHOLD,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);

            // CPUアラーム閾値
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getAlarmCpuThreshold());
            bodies[bodyIndex + 25] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_ALARM_CPU_THRESHOLD,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
            
/*            // HTTPステータスエラー回数
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getHttpStatusCount());
            bodies[bodyIndex + 26] =
                    TelegramUtil.createResponseBody(objName, ITEMNAME_JAVAPROCESS_HTTP_EXCEPTION,
                                                    ItemType.ITEMTYPE_LONG, objItemValueArr);
*/
        }

        // 電文オブジェクトを設定する
        Telegram objTelegram = new Telegram();
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(bodies);
        return objTelegram;
    }

    /**
     * 指定したクラス名、メソッド名から計測対象かどうかを返します。<br />
     * 
     * @param className クラス名
     * @param methodName メソッド名
     * @return 計測対象である場合に、<code>ture</code>
     */
    private static boolean isTarget(Invocation invocation)
    {
        boolean isTarget = ExcludeMonitor.isTarget(invocation);
        boolean isExclude = ExcludeMonitor.isExclude(invocation);
        if (isExclude == true)
        {
            return false;
        }
        if (isTarget == true)
        {
            return true;
        }
        return !ExcludeMonitor.isExcludePreffered(invocation);
    }
}
