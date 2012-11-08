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
package jp.co.acroquest.endosnipe.javelin.record;

import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.S2MaxAccumulatedTimeUpdateRecordStrategy;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;

/**
 * seasarのMaxAccumulatedTimeUpdateRecordStrategyに、
 * Javelinログ通知電文の送信処理を追加したストラテジー。
 * 
 * @author eriguchi
 */
public class MaxAccumulatedTimeUpdateRecordStrategy extends
        S2MaxAccumulatedTimeUpdateRecordStrategy
{
    /**
     * アラームを発生させるかどうかを判定します。<br />
     * 
     * @param node {@link CallTreeNode}オブジェクト
     * @return アラームを発生させる場合、<code>true</code>
     */
    public boolean judgeSendExceedThresholdAlarm(final CallTreeNode node)
    {
        boolean result = false;

        Invocation invocation = node.getInvocation();
        if (invocation.getCountFromStartup() > getIgnoreUpdateCount())
        {
            if (node.getAccumulatedTime() > invocation.getAlarmThreshold())
            {
                invocation.setAlarmThreshold(node.getAccumulatedTime());
                result = true;
            }
            
            long cpuTime = node.getCpuTime() / 1000000;
            if (cpuTime > invocation.getAlarmCpuThreshold())
            {
                invocation.setAlarmCpuThreshold(cpuTime);
                result = true;
            }
        }

        return result;
    }
    
    /**
     * Javelinログ通知電文を送信するコールバックオブジェクトを作成する。
     * 
     * @param node CallTreeNode
     * @return Javelinログ通知電文を送信するコールバックオブジェクト
     */
    @Override
    public JavelinLogCallback createCallback(final CallTreeNode node)
    {
        // アラーム閾値を超えていた場合のみJavelinログ通知電文を送信する。
        if (this.judgeSendExceedThresholdAlarm(node) == false)
        {
            return null;
        }

        return createCallback();
    }

    /**
     * Javelinログ通知電文を送信するコールバックオブジェクトを作成する。
     * 
     * @return コールバック
     */
    @Override
    public JavelinLogCallback createCallback()
    {
        return new JvnFileNotifyCallback();
    }
}
