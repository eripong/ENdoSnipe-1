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
package jp.co.acroquest.endosnipe.javelin;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;

/**
 * TATの値が設定した閾値を越えている場合、ログファイル出力、アラーム通知を行う。
 * @author eriguchi
 *
 */
public class S2DefaultRecordStrategy implements RecordStrategy
{
    private final JavelinConfig config_ = new JavelinConfig();
    
    /** 設定されていた際、常時出力しない、と判定するThreshold */
    private static final long ALWAYS_FALSE_THRESHOLD = -1;

    /**
     * アラームを通知するかどうか判定する</br>
     * AccumulatedTimeがjavelin.alarmThresholdに設定した値以上のときに出力する。
     * @param node CallTreeNode
     * @return true:アラーム通知を行う、false：アラーム通知を行わない。
     */
    public boolean judgeSendExceedThresholdAlarm(final CallTreeNode node)
    {
        long alarmThreshold = node.getInvocation().getAlarmThreshold();
        if (alarmThreshold == Invocation.THRESHOLD_NOT_SPECIFIED)
        {
            //JavelinConfigの閾値が-1と設定されていた場合、本判定によるログ出力は行わない
            if(this.config_.getAlarmCpuThreashold() == ALWAYS_FALSE_THRESHOLD)
            {
                return false;
            }
            alarmThreshold = this.config_.getAlarmThreshold();
        }
        if (node.getAccumulatedTime() >= alarmThreshold)
        {
            return true;
        }
        return false;
    }

    /**
     * 何もしない。
     */
    public void postJudge()
    {
        // Do Nothing
    }

    /**
     * 何もしない。
     * @param node CallTreeNode
     * @return null
     */
    public JavelinLogCallback createCallback(final CallTreeNode node)
    {
        // Do Nothing
        return null;
    }

    /**
     * 何もしない。
     * @return null
     */
    public JavelinLogCallback createCallback()
    {
        // Do Nothing
        return null;
    }
}
