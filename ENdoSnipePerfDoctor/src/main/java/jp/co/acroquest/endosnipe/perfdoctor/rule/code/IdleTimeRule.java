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
 * CPUのアイドル時間が、閾値を超えたことを検出します。<br/>
 * JavelinLogElementを解析し、実行時間とCPU時間の差が閾値に達した場合、
 * 閾値(単位:msec)、アイドル時間(単位:msec)を出力します。。<br/><br/>
 * 
 * 判定内容<br/> 
 * <li>baseInfo[ID] が「Call」であること。 
 * <li>detailInfo[JMXInfo] の duration と thread.currentThreadCpuTime.delta の差が閾値以上であること。
 * 
 * @author fujii
 */
public class IdleTimeRule extends SingleElementRule implements JavelinLogConstants
{
    /** アイドル時間の閾値(単位:msec) */
    public long                 threshold;

    /** 解析情報を取得できない場合のログメッセージ */
    private static final String MESSAGE_NO_JMXINFO =
                                                     Messages.getMessage("endosnipe.perfdoctor.rule.code.MethodPureCpuUsageRule.InfoNotGet",
                                                                         EXTRAPARAM_IDLETIME);

    /**
     * {@inheritDoc}
     */
    @Override
    public void doJudgeElement(final JavelinLogElement element)
    {
        // メソッドの戻りでない場合は、判定を行いません。
        List<?> baseInfo = element.getBaseInfo();
        String id = (String)baseInfo.get(JavelinLogColumnNum.ID);
        if (JavelinConstants.MSG_CALL.equals(id) == false)
        {
            return;
        }

        // スレッドのCPU時間を取得します。
        Map<String, String> argsInfo =
                                       JavelinLogUtil.parseDetailInfo(element,
                                                                      JavelinParser.TAG_TYPE_JMXINFO);
        String cpuTimeStr = argsInfo.get(JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA);

        // 実行時間を取得します。
        Map<String, String> extraInfo =
                                        JavelinLogUtil.parseDetailInfo(element,
                                                                       JavelinParser.TAG_TYPE_EXTRAINFO);
        String execTimeStr = extraInfo.get(EXTRAPARAM_DURATION);

        if (cpuTimeStr == null || execTimeStr == null)
        {
            // 解析情報を取得できない場合は、判定を行いません。
            log(MESSAGE_NO_JMXINFO, element, null);
            return;
        }

        double cpuTimeDouble = 0;
        long cpuTime = 0;
        long execTime = 0;
        try
        {
            cpuTimeDouble = Double.parseDouble(cpuTimeStr);
            cpuTime = (long)cpuTimeDouble;
            execTime = Long.valueOf(execTimeStr);
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        long idleTime = execTime - cpuTime;

        // 閾値を超えていた場合、エラーとします。
        if (idleTime >= this.threshold)
        {
            String threadName = element.getThreadName();
            addError(element, this.threshold, idleTime, threadName);
        }
    }
}
