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
package jp.co.acroquest.endosnipe.perfdoctor.rule.code;

import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * I/O待ちの長い処理を検出するルールです。<br />
 * I/O待ち時間は直接検出できないため、メソッドの TAT から CPU時間、WAIT時間、
 * ブロック時間を引いてI/O待ち時間を検出します。<br />
 * このようにして検出した I/O 待ち時間が閾値よりも長い場合に警告します。<br />
 * 
 * @author akita
 */
public class IOWaitTimeRule extends SingleElementRule
{
    /** メソッドのTATを表す文字列 */
    private static final String DURATION                                      = "duration";

    /** ExtraInfoを表す文字列 */
    private static final String EXTRA_INFO                                    = "ExtraInfo";

    /**
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadCpuTimeパラメータの差分
     * 現在のスレッドの合計 CPU 時間の差分をナノ秒単位で返します。
     */
    private static final String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA =
                                                                                "thread.currentThreadCpuTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedTimeパラメータ
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドが通知を待機した
     * およその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    private static final String JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA  =
                                                                                "thread.threadInfo.waitedTime.delta";

    /**  
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedTimeパラメータの差分
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドがモニターに入るか
     * 再入するのをブロックしたおよその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    private static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA =
                                                                                "thread.threadInfo.blockedTime.delta";

    /** 警告と判断する検出値のデフォルト値。 */
    private static final int    DEFAULT_THRESHOLD                             = 5000;

    /** 検出値の閾値。この値を超えた際に警告を生成する。*/
    public long                 threshold                                     = DEFAULT_THRESHOLD;

    /**
     * CALLログ中のメソッドのTATの値を調査し、 閾値を超えていた際には警告する。
     * 
     * @param javelinLogElement
     *            ログの要素
     * 
     */
    @Override
    public void doJudgeElement(final JavelinLogElement javelinLogElement)
    {
        // 識別子が"Call"でない場合は判定しない。
        String type = javelinLogElement.getBaseInfo().get(JavelinLogColumnNum.ID);
        boolean isCall = JavelinConstants.MSG_CALL.equals(type);
        if (isCall == false)
        {
            return;
        }

        if (JavelinLogUtil.isExistTag(javelinLogElement, JavelinParser.TAG_TYPE_JMXINFO) == false)
        {
            // 計測時にJMX情報を取得していない場合は判定しない
            return;
        }

        // ExtraInfoの内容を表すMapを取得する。
        Map<String, String> extraInfo =
                                        JavelinLogUtil.parseDetailInfo(javelinLogElement,
                                                                       EXTRA_INFO);
        //JMX情報を保持したmapを取得する。
        Map<String, String> jmxInfo =
                                      JavelinLogUtil.parseDetailInfo(javelinLogElement,
                                                                     JavelinParser.TAG_TYPE_JMXINFO);

        // ExtraInfoの情報を保持したmapよりメソッドのTATの値を得る。
        String durationString = extraInfo.get(DURATION);
        //JMX情報のmapより、Wait時間の値を得る。
        String waitTimeString = jmxInfo.get(JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA);
        //JMX情報のmapより、メソッドのCPU時間の値を得る。CPU時間をmsecに変換する。
        String cpuTimeStr = jmxInfo.get(JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA);
        //JMX情報のmapより、ブロック時間の値を得る。
        String blockTimeString = jmxInfo.get(JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA);

        //TAT,Wait時間,CPU時間、ブロック時間をそれぞれ型変換する。（CPU時間はnsecからmsecに単位を変更する）

        long waitTime = 0;
        double cpuTimeDouble = 0;
        long cpuTime = 0;
        long blockTime = 0;
        long duration = 0;
        try
        {
            if (waitTimeString != null)
            {
                waitTime = Long.parseLong(waitTimeString);
            }
            if (cpuTimeStr != null)
            {
                cpuTimeDouble = Double.parseDouble(cpuTimeStr);
            }
            cpuTime = (long)cpuTimeDouble;
            if (blockTimeString != null)
            {
                blockTime = Long.parseLong(blockTimeString);
            }
            if (durationString != null)
            {
                duration = Long.parseLong(durationString);
            }
        }
        catch (NumberFormatException nfex)
        {
            return;
        }
        /*メソッドのTATとCPU時間、Wait時間、ブロック時間の和との差を求める。
        (メソッドのTAT)-((CPU時間)+(Wait時間)+(ブロック時間))
        */
        long status = duration - (waitTime + cpuTime + blockTime);
        // もし検出値が閾値に達するのであれば、警告を出す。
        if (status >= this.threshold)
        {
            addError(javelinLogElement, this.threshold, status);
        }
    }
}
