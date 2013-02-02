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
import jp.co.acroquest.endosnipe.common.config.JavelinConfigUtil;
import jp.co.acroquest.endosnipe.javelin.CallTreeNode;
import jp.co.acroquest.endosnipe.javelin.RecordStrategy;
import jp.co.acroquest.endosnipe.javelin.log.JavelinLogCallback;
import jp.co.acroquest.endosnipe.javelin.util.concurrent.ConcurrentHashMap;

/**
 * 同一SQLの実行計画出力から一定時間超えた場合に、実行計画の記録・通知を行うRecordStrategy。
 * 閾値はjavelin.jdbc.planIntervalで指定する。
 * 
 * @author tsukano
 */
public class SqlPlanStrategy implements RecordStrategy
{
    /** 実行s計画を保存する際のキー */
    private static final String JDBC_PLAN_KEY = "jdbc.plan";

    /** 同一SQLの実行計画出力をアラームにする閾値 */
    private long threshold_;

    /** 閾値を表すプロパティ名 */
    private static final String PLAN_INTERVAL_KEY =
            JavelinConfig.JAVELIN_PREFIX + "jdbc.planInterval";

    /** 閾値のデフォルト(1日) */
    private static final int DEFAULT_PLAN_INTERVAL = 60000 * 60 * 24;

    /** CallTreeNodeに登録する際のキー */
    public static final String KEY = "SqlPlanStrategy";

    /**
     * 実行計画の最終出力日時を保持するマップ。
     * 全トランザクションにまたがって保持している。
     * key=SQL文のhashCode、value=実行計画の最終出力日時
     */
    private static Map<Integer, Long> sqlPlanMap__ = new ConcurrentHashMap<Integer, Long>();

    /**
     * プロパティからplanIntervalを読み込む。
     */
    public SqlPlanStrategy()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        threshold_ = configUtil.getLong(PLAN_INTERVAL_KEY, DEFAULT_PLAN_INTERVAL);
    }

    /**
     * SQLの実行計画出力用のSQLがあるか判定します。<br />
     * 
     * @param sql 実行計画出力時間を記録するSQL
     * 
     * @return 実行計画出力用のSQLが登録されており、かつ期限が切れていない場合は <code>true</code>
     */
    public boolean existPlanOutputSql(String sql)
    {
        boolean result = false;
        if (sql == null)
        {
            return result;
        }

        long now = System.currentTimeMillis();
        Integer hashCode = Integer.valueOf(sql.hashCode());

        Long lastOutputTimeLong = sqlPlanMap__.get(hashCode);
        if (lastOutputTimeLong != null)
        {
            long lastOutputTime = lastOutputTimeLong.longValue();
            if (now - lastOutputTime <= threshold_)
            {
                result = true;
            }
        }

        return result;
    }

    /**
     * SQLの実行計画出力用のSQLを登録します。<br />
     * 
     * @param sql 実行計画出力用のSQL
     */
    public void recordPlanOutputSql(String sql)
    {
        if (sql == null)
        {
            return;
        }
        long now = System.currentTimeMillis();
        Integer hashCode = Integer.valueOf(sql.hashCode());

        sqlPlanMap__.put(hashCode, now);
    }

    /**
     * Javelilnログは出力しない。
     * 保存しているSQLが多くなった場合に削除する。
     * 
     * @param node ノード。
     * @return Javelinログファイルを出力するかどうか。
     */
    public boolean judgeGenerateJaveinFile(CallTreeNode node)
    {
        if (sqlPlanMap__.size() > new JavelinConfig().getRecordInvocationMax())
        {
            sqlPlanMap__.clear();
        }

        return false;
    }

    public void setExecPlan(CallTreeNode node, String[] execPlan)
    {
        node.getInvocation().putOptValue(JDBC_PLAN_KEY, execPlan);
    }

    public String[] getExecPlan(CallTreeNode node)
    {
        return (String[])node.getInvocation().getOptValue(JDBC_PLAN_KEY);
    }

    /**
     * @see {@link #judgeGenerateJaveinFile(CallTreeNode)}
     */
    public boolean judgeSendExceedThresholdAlarm(CallTreeNode node)
    {
        return judgeGenerateJaveinFile(node);
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
    public JavelinLogCallback createCallback(CallTreeNode node)
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
