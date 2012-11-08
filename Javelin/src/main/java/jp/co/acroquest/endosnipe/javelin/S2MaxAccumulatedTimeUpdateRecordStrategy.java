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
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;

/**
 * 呼び出し積算時間の最大値が更新された場合に、記録・通知を行うRecordStrategy。 ただし、不必要な記録・通知を防ぐため、更新回数が
 * javelin.maxAccumulatedTimeUpdate.ignoreUpdateCountで指定された 回数以下の場合は記録・通知を行わない。
 * 
 * @author tsukano
 */
public class S2MaxAccumulatedTimeUpdateRecordStrategy implements RecordStrategy
{
    /** 更新回数を無視する閾値 */
    private final int ignoreUpdateCount_;

    /** 更新回数を無視する閾値を表すプロパティ名 */
    private static final String IGNOREUPDATECOUNT_KEY =
            JavelinConfig.JAVELIN_PREFIX + "maxAccumulatedTimeUpdate.ignoreUpdateCount";

    /** 更新回数を無視する閾値のデフォルト */
    private static final int DEFAULT_IGNOREUPDATECOUNT = 3;

    /**
     * プロパティからignoreUpdateCountを読み込む。
     */
    public S2MaxAccumulatedTimeUpdateRecordStrategy()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        this.ignoreUpdateCount_ =
                configUtil.getInteger(IGNOREUPDATECOUNT_KEY, DEFAULT_IGNOREUPDATECOUNT);
    }

    /**
     * アラームを発生させるかどうかを判定します。<br />
     * 
     * @param node {@link CallTreeNode}オブジェクト
     * @return アラームを発生させる場合、<code>true</code>
     */
    public boolean judgeSendExceedThresholdAlarm(final CallTreeNode node)
    {
        if (node.getAccumulatedTime() >= node.getInvocation().getMaxAccumulatedTime()
                && node.getInvocation().getCountFromStartup() > this.ignoreUpdateCount_)
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

    public int getIgnoreUpdateCount()
    {
        return ignoreUpdateCount_;
    }
}
