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
import jp.co.acroquest.endosnipe.perfdoctor.Messages;
import jp.co.acroquest.endosnipe.perfdoctor.rule.SingleElementRule;

/**
 * メソッドの開始から終了までの間の純粋CPU時間が、閾値を超えたことを検出する。</br> メソッド終了部分のJavelinLogElementを解析し、
 * メソッドの開始から終了までに使用したCPU時間が閾値に達した閾値(単位:msec)、CPU時間(単位:msec)を出力する。<br>
 * 
 * 判定内容</br> <li>baseInfo[ID] が「Return」であること。 <li>detailInfo[JMXInfo] の
 * thread.currentThreadCpuTime.delta の値が閾値以上であること。
 * 
 * @author Sakamoto
 */
public class MethodPureCpuUsageRule extends SingleElementRule implements JavelinLogConstants
{
    /** 警告と判断するCPU時間のデフォルト値。 */
    private static final int    DEFAULT_THRESHOLD  = 3000;

    /** CPU時間の閾値(単位:msec) */
    public long                 threshold          = DEFAULT_THRESHOLD;

    /** 解析情報を取得できない場合のログメッセージ */
    private static final String MESSAGE_NO_JMXINFO =
                                                     Messages.getMessage("endosnipe.perfdoctor.rule.code.MethodPureCpuUsageRule.InfoNotGet",
                                                                         EXTRAPARAM_PURECPUTIME);

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

        // 純粋CPU時間を取得する
        Map<String, String> extraInfo =
                                        JavelinLogUtil.parseDetailInfo(element,
                                                                       JavelinParser.TAG_TYPE_EXTRAINFO);
        String cpuTimeStr = extraInfo.get(EXTRAPARAM_PURECPUTIME);

        if (cpuTimeStr == null)
        {
            // 解析情報を取得できない場合は、判定を行わない
            return;
        }

        double cpuTimeDouble = 0;
        long cpuTime = 0;
        cpuTimeDouble = Double.parseDouble(cpuTimeStr);
        cpuTime = (long)cpuTimeDouble;

        // 閾値を超えていた場合、エラーとする
        if (cpuTime >= this.threshold)
        {
            String threadName = element.getThreadName();
            addError(element, this.threshold, cpuTime, threadName);
        }
    }
}
