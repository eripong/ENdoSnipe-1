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

import java.util.List;
import java.util.Map;

import jp.co.acroquest.endosnipe.common.parser.JavelinConstants;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogColumnNum;
import jp.co.acroquest.endosnipe.common.parser.JavelinLogConstants;
import jp.co.acroquest.endosnipe.javelin.JavelinLogUtil;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinLogElement;
import jp.co.acroquest.endosnipe.javelin.parser.JavelinParser;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * メソッドの開始から終了までの間のCPU時間が、閾値を超えたことを検出する。</br> メソッド終了部分のJavelinLogElementを解析し、
 * メソッドの開始から終了までに使用したCPU時間が閾値に達した閾値(単位:msec)、CPU時間(単位:msec)を出力する。<br>
 * 
 * 判定内容</br> <li>baseInfo[ID] が「Return」であること。 <li>detailInfo[JMXInfo] の
 * thread.currentThreadCpuTime.delta の値が閾値以上であること。
 * 
 * @author tsukano
 */
public class MethodCpuUsageRule extends SingleElementRule implements JavelinLogConstants
{
    /** CPU時間の閾値(単位:msec) */
    public long threshold;

    /** CPU時間の閾値の最小値(単位:msec) */
    // private static final long MIN_THRESHOLD = 0;
    /** CPU時間の閾値の最大値(単位:msec) */
    // private static final long MAX_THRESHOLD = Long.MAX_VALUE;
    // 現バージョンではvalidationは扱わない。
    // /** validateエラーのログメッセージ */
    // private static final String MESSAGE_VALIDATE_ERROR
    // = "threshold(%1d)が設定可能範囲(%2d - %3d)です。";
    // 現時点では使用しない。validateを実装する際にはこれを用いる。
    /**
     * {@inheritDoc}</br> thresholdに設定した値のvalidateを行う。
     */
    /*
     * @Override public void validate() { if (this.threshold < MIN_THRESHOLD ||
     * MAX_THRESHOLD < this.threshold) { String message =
     * String.format(MESSAGE_VALIDATE_ERROR, this.threshold, MIN_THRESHOLD,
     * MAX_THRESHOLD); log(message, null, null);
     * 
     * // TODO:validateを実装した際に修正すること。 addValidationError(MESSAGE_VALIDATE_ERROR,
     * this.threshold, MIN_THRESHOLD, MAX_THRESHOLD); } }
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void doJudgeElement(final JavelinLogElement element)
    {
        // メソッドの戻りでない場合は、判定を行わない
        List<String> baseInfo = element.getBaseInfo();
        String id = baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(id) == false)
        {
            return;
        }

        // SQL実行は除外する
        String className = baseInfo.get(JavelinLogColumnNum.CALL_CALLEE_CLASS);
        if (isSqlExec(className) == true)
        {
            return;
        }

        // JMX情報からCPU時間を取得する
        Map<String, String> map =
                                  JavelinLogUtil.parseDetailInfo(element,
                                                                 JavelinParser.TAG_TYPE_JMXINFO);
        String cpuTimeStr = map.get(JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA);

        //CPU時間が取得できない場合、判定を行わない。
        if (cpuTimeStr == null)
        {
            return;
        }

        double cpuTimeDouble = 0;
        long cpuTime = 0;
        try
        {
            cpuTimeDouble = Double.parseDouble(cpuTimeStr);
            cpuTime = (long)cpuTimeDouble;
        }
        catch (NumberFormatException ex)
        {
            return;
        }

        // 閾値を超えていた場合、エラーとする
        if (cpuTime >= this.threshold)
        {
            String threadName = element.getThreadName();
            addError(element, this.threshold, cpuTime, threadName);
        }
    }

}
