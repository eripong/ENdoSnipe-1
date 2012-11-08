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

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.RecordStrategy;
import jp.co.acroquest.endosnipe.javelin.bean.Invocation;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;

/**
 * CPU時間、もしくはTATの閾値で出力するかどうかを判定するクラス
 * 
 * @author fujii
 * 
 */
public class CpuTimeRecordStrategy implements RecordStrategy
{
    /** 単位をマイクロ秒から秒に変えるための値 */
    private static final int NANO_TO_MILLI = 1000000;

    /** Javelinの設定値 */
    private final JavelinConfig config_ = new JavelinConfig();
    
    /** 設定されていた際、常時出力しない、と判定するThreshold */
    private static final long ALWAYS_FALSE_THRESHOLD = -1;

    /**
     * アラームを通知するかどうか判定する</br>
     * CpuTimeがjavelin.alarmCpuThresholdに設定した値以上のときに出力する。
     * 
     * @param node CallTreeノード
     * @return true:ログ通知する、false:ログ通知しない。
     */
    public boolean judgeSendExceedThresholdAlarm(final CallTreeNode node)
    {
        boolean exceedAlarmThreshold = isAlarmThreshold(node);
        boolean exceedAlarmCpuThreshold = isAlarmCpuThresold(node);

        if (exceedAlarmThreshold || exceedAlarmCpuThreshold)
        {
            return true;
        }
        return false;
    }

    /**
     * 設定した警告発生のCPU時間の閾値を超えているか判定する。
     * 
     * @param node CallTreeノード
     * @return 設定したCPU時間を超えている。
     */
    private boolean isAlarmCpuThresold(final CallTreeNode node)
    {
        long alarmCpuThreshold = node.getInvocation().getAlarmCpuThreshold();
        if (alarmCpuThreshold == Invocation.THRESHOLD_NOT_SPECIFIED)
        {
            //JavelinConfigの閾値が-1と設定されていた場合、本判定によるログ出力は行わない
            if(this.config_.getAlarmCpuThreashold() == ALWAYS_FALSE_THRESHOLD)
            {
                return false;
            }
            alarmCpuThreshold = this.config_.getAlarmCpuThreashold();
        }
        if (node.getCpuTime() / NANO_TO_MILLI >= alarmCpuThreshold)
        {
            return true;
        }
        return false;
    }

    /**
     * 設定した警告発生の時間の閾値を超えているか判定する。
     * 
     * @param node CallTreeノード
     * @return 設定した警告発生閾値を超えている。
     */
    private boolean isAlarmThreshold(final CallTreeNode node)
    {
        long alarmThreshold = node.getInvocation().getAlarmThreshold();
        if (alarmThreshold == Invocation.THRESHOLD_NOT_SPECIFIED)
        {
            //JavelinConfigの閾値が-1と設定されていた場合、本判定によるログ出力は行わない
            if(this.config_.getAlarmThreshold() == ALWAYS_FALSE_THRESHOLD)
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
     * Javelinログ通知電文を送信するコールバックオブジェクトを作成する。
     * 
     * @param node CallTreeノード
     * @return コールバック
     */
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
    public JavelinLogCallback createCallback()
    {
        return new JvnFileNotifyCallback();
    }
}
