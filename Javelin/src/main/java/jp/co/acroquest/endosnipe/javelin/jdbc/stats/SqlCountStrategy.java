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
package jp.co.acroquest.endosnipe.javelin.jdbc.stats;

import java.util.Map;

import jp.co.acroquest.endosnipe.common.config.JavelinConfig;
import jp.co.acroquest.endosnipe.common.event.EventConstants;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.RecordStrategy;
import jp.co.acroquest.endosnipe.javelin.StatsJavelinRecorder;
import jp.co.acroquest.endosnipe.javelin.event.CommonEvent;
import jp.co.acroquest.endosnipe.javelin.event.SqlCountOverEvent;
import jp.co.acroquest.endosnipe.javelin.jdbc.common.JdbcJavelinConfig;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;
import jp.co.acroquest.endosnipe.javelin.util.HashMap;
import jp.co.acroquest.endosnipe.javelin.util.ThreadUtil;

/**
 * 同一トランザクション内で、同一SQLの呼び出し回数が一定回数を超えた場合に、記録・通知を行うRecordStrategy。
 * 閾値はjavelin.jdbc.sqlcountで指定する。
 * 
 * @author tsukano
 */
public class SqlCountStrategy implements RecordStrategy
{
    /** 同一SQLの呼び出し回数をアラームにする閾値 */
    private final long threshold_;

    /**
     * SQLの呼び出し回数を保持するマップ。
     * key=SQL文、value=呼び出し回数
     */
    private final Map<String, Integer> sqlCountMap_ = new HashMap<String, Integer>();

    /**
     * プロパティからsqlcountを読み込む。
     */
    public SqlCountStrategy()
    {
        JdbcJavelinConfig config = new JdbcJavelinConfig();
        threshold_ = config.getSqlcount();
    }

    /**
     * SQLの呼び出し回数のカウントを増やす。
     * @param sql 呼び出し回数を増やすSQL
     */
    public void incrementSQLCount(final String sql)
    {
        synchronized (sqlCountMap_)
        {
            int newValue;
            if (sqlCountMap_.containsKey(sql))
            {
                // 既に呼び出し経験があるSQLは呼び出し回数を1つ増やす
                int old = sqlCountMap_.get(sql);
                newValue = old + 1;
            }
            else
            {
                // 既に呼び出し経験がないSQLは呼び出し回数を1にする
                newValue = 1;
            }

            sqlCountMap_.put(sql, newValue);
            if (newValue >= threshold_)
            {
                CommonEvent event = new SqlCountOverEvent();
                event.addParam(EventConstants.PARAM_SQLCOUNT_THRESHOLD, String.valueOf(threshold_));
                event.addParam(EventConstants.PARAM_SQLCOUNT_ACTUAL, String.valueOf(newValue));
                event.addParam(EventConstants.PARAM_SQLCOUNT_SQL, sql);
                JavelinConfig config = new JavelinConfig();
                StackTraceElement[] stacktraces = ThreadUtil.getCurrentStackTrace();
                String stackTrace = ThreadUtil.getStackTrace(stacktraces, config.getTraceDepth());
                event.addParam(EventConstants.PARAM_SQLCOUNT_STACKTRACE, stackTrace);
                StatsJavelinRecorder.addEvent(event);
            }
        }
    }

    /**
     * 常にfalseを返します。
     * 
     * @param node 使用しません。
     * @return 常にfalse。 
     */
    public boolean judgeGenerateJaveinFile(final CallTreeNode node)
    {
        return false;
    }

    /**
     * 常にfalseを返します。
     * 
     * @param node 使用しません。
     * @return 常にfalse。 
     */
    public boolean judgeSendExceedThresholdAlarm(final CallTreeNode node)
    {
        return judgeGenerateJaveinFile(node);
    }

    /**
     * SQLの呼び出し回数の情報をクリアする。
     */
    public void postJudge()
    {
        synchronized (sqlCountMap_)
        {
            sqlCountMap_.clear();
        }
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
